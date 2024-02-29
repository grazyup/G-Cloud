package com.grazy.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.common.event.file.DeleteFileEvent;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.FileUtils;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.*;
import com.grazy.modules.file.domain.GCloudFile;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.enums.FileTypeEnum;
import com.grazy.modules.file.enums.FolderFlagEnum;
import com.grazy.modules.file.mapper.GCloudUserFileMapper;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.GCloudUserFileVO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaofu
 * @description 针对表【g_cloud_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-01-24 17:42:44
 */

@Service(value = "GCloudUserFileService")
public class GCloudUserFileServiceImpl extends ServiceImpl<GCloudUserFileMapper, GCloudUserFile> implements GCloudUserFileService, ApplicationContextAware {

    @Resource
    private GCloudUserFileMapper gCloudUserFileMapper;

    @Resource
    private GCloudFileService gCloudFileService;

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    /**
     * 创建文件夹信息
     *
     * @param createFolderContext 文件夹上下文信息
     * @return 文件夹id
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getUserId(), createFolderContext.getParentId(),
                createFolderContext.getFolderName(), null, null, FolderFlagEnum.YES, null);
    }


    /**
     * 查询用户的根文件夹信息
     *
     * @param userId 用户Id
     * @return 用户根文件夹信息实体
     */
    @Override
    public GCloudUserFile getFIleInfo(Long userId) {
        LambdaQueryWrapper<GCloudUserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUserFile::getUserId,userId)
                .eq(GCloudUserFile::getDelFlag,DelFlagEnum.NO.getCode())
                .eq(GCloudUserFile::getFolderFlag,FolderFlagEnum.YES.getCode())
                .eq(GCloudUserFile::getParentId,FileConstants.TOP_PARENT_ID);
        return getOne(lambdaQueryWrapper);
    }


    /**
     * 查询用户的文件列表
     * 1.如果用户的 parentId 为 -1，则是执行按照文件类型查询文件列表
     * 2.parentId 存在具体值，则为查询的是当前文件夹中的文件列表
     *
     * @param queryFileListContext 查询列表上下文信息
     * @return 文件列表数据Vo
     */
    @Override
    public List<GCloudUserFileVO> getFileList(QueryFileListContext queryFileListContext) {
        return gCloudUserFileMapper.selectFileList(queryFileListContext);
    }


    /**
     * 文件重命名
     * 1.校验更新文件名的条件
     * 2.执行文件重命名操作
     *
     * @param updateFilenameContext 重命名上下文信息
     */
    @Override
    public void updateFilename(UpdateFilenameContext updateFilenameContext) {
        checkUpdateFilenameCondition(updateFilenameContext);
        doUpdateFilename(updateFilenameContext);
    }


    /**
     * 批量删除文件
     * 1.校验文件删除的条件
     * 2.执行批量删除文件操作
     * 3.发布批量删除文件事件，给其他模块订阅使用
     *
     * @param deleteFileContext 文件删除上下文信息
     */
    @Override
    public void deleteFile(DeleteFileContext deleteFileContext) {
        checkDeleteFileCondition(deleteFileContext);
        doDeleteFile(deleteFileContext);
        pushDeleteFileEvent(deleteFileContext);
    }


    /**
     * 文件秒传
     * 1.通过文件的唯一标识，查找该用户对应的文件实体记录
     * 2.不存在该记录，返回秒传失败
     * 3.存在记录，直接挂载当前文件的关联关系，返回秒传成功
     *
     * @param context 文件秒传上下文信息
     * @return 秒传结果
     */
    @Override
    public boolean secUpload(SecUploadFileContext context) {
        List<GCloudFile> dbFileList = getFileListByUserIdAndIdentifier(context.getIdentifier(),context.getUserId());
        if(CollectionUtils.isEmpty(dbFileList)){
            return false;
        }
        GCloudFile record = dbFileList.get(GCloudConstants.ZERO_INT);
        saveUserFile(context.getUserId(),context.getParentId(),context.getFilename(),
                record.getFileId(), record.getFileSizeDesc(), FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())));
        return true;
    }





    /**
     * 保存用户文件到数据库
     *
     * @return 文件ID
     */
    private Long saveUserFile(Long userId, Long parentId, String filename, Long realFileId,
                              String fileSizeDesc, FolderFlagEnum folderFlag, Integer fileType) {
        //组装userFile文件信息
        GCloudUserFile entity = assembleGCloudUserFile(userId, parentId, filename, realFileId
                , fileSizeDesc, folderFlag, fileType);
        if (!this.save(entity)) {
            throw new GCloudBusinessException("文件保存失败！");
        }
        return entity.getFileId();
    }


    /**
     * 组装userFile文件信息
     * 1.填充实体信息
     * 2.处理统一父目录下文件命名的重复问题
     *
     * @return 用户文件对象
     */
    private GCloudUserFile assembleGCloudUserFile(Long userId, Long parentId, String filename, Long realFileId,
                                                  String fileSizeDesc, FolderFlagEnum folderFlag, Integer fileType) {
        GCloudUserFile entity = new GCloudUserFile();
        //雪花算法生成文件记录Id
        entity.setFileId(IdUtil.get());
        entity.setParentId(parentId);
        entity.setUserId(userId);
        entity.setFilename(filename);
        entity.setFileType(fileType);
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setFolderFlag(folderFlag.getCode());
        entity.setRealFileId(realFileId);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userId);
        entity.setCreateUser(userId);

        //处理重复文件名
        handleDuplicateFilename(entity);
        return entity;
    }


    /**
     * 处理同一父目录下重复文件名问题
     * 按照系统级规则重命名文件
     *
     * @param entity 文件对象
     */
    private void handleDuplicateFilename(GCloudUserFile entity) {
        String filename = entity.getFilename(),
                newFilenameWithoutSuffix,  //文件名中 文件后缀.的前半部分
                newFilenameSuffix;      // 文件后缀，包括.
        int filenamePointPosition = filename.lastIndexOf(GCloudConstants.POINT_STR);
        if (filenamePointPosition == GCloudConstants.MINUS_ONE_STR) {
            //没有后缀,是一个文件夹
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = GCloudConstants.EMPTY_STR;
        } else {
            //是一个文件
            newFilenameWithoutSuffix = filename.substring(GCloudConstants.ZERO_INT, filenamePointPosition);  //文件名
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, GCloudConstants.EMPTY_STR);   //文件后缀
        }

        //统计重复文件名的数量
        int count = countDuplicateFilename(entity, newFilenameWithoutSuffix);
        if (count == GCloudConstants.ZERO_INT) return;

        //拼接新文件名称
        String newFilename = assembleNewFileName(count, newFilenameWithoutSuffix, newFilenameSuffix);

        //重新赋值给实体对象
        entity.setFilename(newFilename);
    }


    /**
     * 拼装新文件名称
     * 拼装规则参考操作系统重复文件名称的重命名规范
     *
     * @param count
     * @param newFilenameWithoutSuffix
     * @param newFileNameSuffix
     * @return
     */
    private String assembleNewFileName(int count, String newFilenameWithoutSuffix, String newFileNameSuffix) {
        return new StringBuilder(newFilenameWithoutSuffix)
                .append(FileConstants.CN_LEFT_PARENTHESES_STR)
                .append(count)
                .append(FileConstants.CN_RIGHT_PARENTHESES_STR)
                .append(newFileNameSuffix)
                .toString();
    }


    /**
     * 统计同一个父目录下的重复文件名数量
     *
     * @param newFilenameWithoutSuffix 文件名的前缀名称
     * @return 重复数量
     */
    private int countDuplicateFilename(GCloudUserFile entity, String newFilenameWithoutSuffix) {
        LambdaQueryWrapper<GCloudUserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUserFile::getParentId, entity.getParentId())
                .eq(GCloudUserFile::getFolderFlag, entity.getFolderFlag())
                .eq(GCloudUserFile::getUserId, entity.getUserId())
                .eq(GCloudUserFile::getDelFlag, entity.getDelFlag())
                .likeRight(GCloudUserFile::getFilename, newFilenameWithoutSuffix);
        return this.count(lambdaQueryWrapper);
    }


    /**
     * 执行文件重命名操作
     *
     * @param context 重命名上下文信息
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        GCloudUserFile entity = context.getEntity();
        entity.setFilename(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());
        entity.setUpdateTime(new Date());
        if(!updateById(entity)){
            throw new GCloudBusinessException("文件重命名失败！");
        }
    }


    /**
     * 校验更新文件名称的条件
     * 1.文件ID是否合法
     * 2.用户是否拥有权限修改
     * 3.新的文件名称在同一父目录下的是否与兄弟文件名称存在一致
     * 4.新旧文件名是否一致
     *
     * @param context 重命名上下文信息
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {
        Long fileId = context.getFileId();
        GCloudUserFile gCloudUserFile = getById(fileId);

        if (Objects.isNull(gCloudUserFile)) {
            throw new GCloudBusinessException("错误的文件ID");
        }

        if (!Objects.equals(gCloudUserFile.getUserId(), context.getUserId())) {
            throw new GCloudBusinessException("当前登录用户没有修改该文件名称的权限");
        }

        if (Objects.equals(gCloudUserFile.getFilename(), context.getNewFilename())) {
            throw new GCloudBusinessException("不能与旧文件名一致");
        }

        LambdaQueryWrapper<GCloudUserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUserFile::getParentId, gCloudUserFile.getParentId())
                .eq(GCloudUserFile::getFilename, context.getNewFilename())
                .eq(GCloudUserFile::getDelFlag, DelFlagEnum.NO.getCode());
        int count = count(lambdaQueryWrapper);
        if (count > 0) {
            throw new GCloudBusinessException("该文件名称已被占用");
        }

        context.setEntity(gCloudUserFile);
    }


    /**
     * 对外发布文件删除的事件
     *
     * @param deleteFileContext 文件删除上下文信息
     */
    private void pushDeleteFileEvent(DeleteFileContext deleteFileContext) {
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, deleteFileContext.getFileIdList());
        applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 执行批量删除文件操作
     *
     * @param deleteFileContext 文件删除上下文信息
     */
    private void doDeleteFile(DeleteFileContext deleteFileContext) {
        LambdaUpdateWrapper<GCloudUserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(GCloudUserFile::getFileId, deleteFileContext.getFileIdList())
                .set(GCloudUserFile::getDelFlag, DelFlagEnum.YES.getCode())
                .set(GCloudUserFile::getUpdateUser,deleteFileContext.getUserId())
                .set(GCloudUserFile::getUpdateTime, new Date());
        if(!update(lambdaUpdateWrapper)){
            throw new GCloudBusinessException("文件删除失败");
        }
    }


    /**
     * 校验文件删除的条件
     * 1.校验文件ID是否合法
     * 2.校验用户是否有权限删除文件
     *
     * @param deleteFileContext 文件删除上下文信息
     */
    private void checkDeleteFileCondition(DeleteFileContext deleteFileContext) {
        // 参数文件ID是否全部查询到数据库对应文件数据
        List<Long> fileIdList = deleteFileContext.getFileIdList();
        List<GCloudUserFile> dbUserFiles = listByIds(fileIdList);
        if(dbUserFiles.size() != fileIdList.size()){
            throw new GCloudBusinessException("存在不合法的文件记录");
        }

        // 判断参数文件ID与数据库文件ID数量是否一致
        Set<Long> dbFileIdSet = dbUserFiles.stream().map(GCloudUserFile::getFileId).collect(Collectors.toSet());
        int oldSize = dbFileIdSet.size();
        dbFileIdSet.addAll(fileIdList);
        int newSize = dbFileIdSet.size();
        if(oldSize != newSize){
            throw new GCloudBusinessException("存在不合法的文件记录");
        }

        // 判断查询出的文件数据是否都是一个用户的
        Set<Long> dbUserIdSet = dbUserFiles.stream().map(GCloudUserFile::getUserId).collect(Collectors.toSet());
        if(dbUserIdSet.size() != 1){
            throw new GCloudBusinessException("存在不合法的文件记录");
        }

        // 判断用户ID与数据库查询的文件用户ID是否一致
        Long dbUserId = dbUserIdSet.stream().findFirst().get();
        if(!Objects.equals(dbUserId, deleteFileContext.getUserId())){
            throw new GCloudBusinessException("当前登录用户没有删除该文件的权限");
        }
    }


    /**
     * 根据唯一标识与用户ID查询已上传的文件记录
     * @param identifier 文件的唯一标识
     * @param userId 用户ID
     * @return 文件实体集合
     */
    private List<GCloudFile> getFileListByUserIdAndIdentifier(String identifier, Long userId) {
        LambdaQueryWrapper<GCloudFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudFile::getIdentifier,identifier)
                .eq(GCloudFile::getCreateUser, userId);
        return gCloudFileService.list(lambdaQueryWrapper);
    }
}




