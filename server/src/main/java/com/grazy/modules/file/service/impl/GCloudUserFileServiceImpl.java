package com.grazy.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.grazy.common.event.file.DeleteFileEvent;
import com.grazy.common.utils.HttpUtil;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.FileUtils;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.*;
import com.grazy.modules.file.converter.FileConverter;
import com.grazy.modules.file.domain.GCloudFile;
import com.grazy.modules.file.domain.GCloudFileChunk;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.enums.FileTypeEnum;
import com.grazy.modules.file.enums.FolderFlagEnum;
import com.grazy.modules.file.mapper.GCloudUserFileMapper;
import com.grazy.modules.file.service.GCloudFileChunkService;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.FileChunkUploadVO;
import com.grazy.modules.file.vo.FolderTreeNodeVo;
import com.grazy.modules.file.vo.UploadChunksVo;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.storage.engine.core.StorageEngine;
import com.grazy.storage.engine.core.context.ReadFileContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
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


    @Resource
    private FileConverter fileConverter;


    @Resource
    private GCloudFileChunkService gCloudFileChunkService;

    @Resource
    private StorageEngine storageEngine;


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
    public List<UserFileVO> getFileList(QueryFileListContext queryFileListContext) {
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
     * 单文件上传
     * 1.上传文件并保存文件的实体记录
     * 2.保存用户上传文件的关联关系记录
     *
     * @param context 文件上传上下文信息
     */
    @Override
    @Transactional
    public void upload(FileUploadContext context) {
        FileSaveContext fileSaveContext = fileConverter.FileUpLoadContextToFileSaveContext(context);
        gCloudFileService.saveFile(fileSaveContext);
        context.setRecord(fileSaveContext.getRecord());
        this.saveUserFile(context.getUserId(), context.getParentId(), context.getFilename(),
                context.getRecord().getFileId(), context.getRecord().getFileSizeDesc(),
                FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())));
    }


    /**
     * 文件分片上传
     * 1.上传分片文件以及保存分片记录到数据库
     * 2.设置是否合并的响应参数
     *
     * @param context 文件分片上传信息
     * @return
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        FileChunkSaveContext fileChunkSaveContext = fileConverter.FileChunkUploadContextToFileChunkSaveContext(context);
        gCloudFileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO fileChunkUploadVO = new FileChunkUploadVO();
        fileChunkUploadVO.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return fileChunkUploadVO;
    }


    /**
     * 获取已上传的分片文件
     *
     * @param context 文件唯一标识
     * @return 已上传的分片文件编号列表Vo
     */
    @Override
    public UploadChunksVo getUploadedChunks(QueryUploadChunkContext context) {
        LambdaQueryWrapper<GCloudFileChunk> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(GCloudFileChunk::getChunkNumber);  //指定一个字段，如有多个只查询第一个
        lambdaQueryWrapper.eq(GCloudFileChunk::getIdentifier,context.getIdentifier())
                .eq(GCloudFileChunk::getCreateUser,context.getUserId())
                .gt(GCloudFileChunk::getExpirationTime, new Date());
        //该方法只查询指定的一个字段并返回
        List<Integer> chunkNumberList = gCloudFileChunkService.listObjs(lambdaQueryWrapper, value -> (Integer) value);
        UploadChunksVo uploadChunksVo = new UploadChunksVo();
        uploadChunksVo.setUploadedChunks(chunkNumberList);
        return uploadChunksVo;
    }


    /**
     * 合并分片文件
     * 1.合并文件并保存文件的实体记录
     * 2.保存用户上传文件的关联关系记录
     *
     * @param context 合并分片文件上下文信息
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        mergeFileChunkAndSaveFileRecord(context);
        this.saveUserFile(context.getUserId(),context.getParentId(),context.getFilename(),
                context.getRecord().getFileId(),context.getRecord().getFileSizeDesc(),
                FolderFlagEnum.NO,FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())));
    }


    /**
     * 文件下载
     * 1.校验下载的文件资源是否存在
     * 2.判断该文件是否属于该用户
     * 3.校验该文件是否是一个文件夹
     * 4.执行文件下载
     *
     * @param context 文件下载上下文参数信息
     */
    @Override
    public void download(FileDownloadContext context) {
        GCloudUserFile record = getById(context.getFileId());
        if(Objects.isNull(record)){
            throw new GCloudBusinessException("文件资源不存在");
        }
        if(!record.getUserId().equals(context.getUserId())){
            throw new GCloudBusinessException("您没有该文件的操作权限");
        }
        if(!FolderFlagEnum.YES.getCode().equals(record.getFolderFlag())){
            throw new GCloudBusinessException("文件夹暂不支持下载");
        }
        doDownload(context.getResponse(),record);
    }

    /**
     * 文件预览
     * 1.校验预览的文件资源是否存在
     * 2.判断该文件是否属于该用户
     * 3.校验该文件是否是一个文件夹
     * 4.执行文件预览
     *
     * @param context 文件预览上下文参数对象
     */
    @Override
    public void preview(FilePreviewContext context) {
        GCloudUserFile record = getById(context.getFileId());
        if(Objects.isNull(record)){
            throw new GCloudBusinessException("文件资源不存在");
        }
        if(!record.getUserId().equals(context.getUserId())){
            throw new GCloudBusinessException("您没有该文件的操作权限");
        }
        if(!FolderFlagEnum.YES.getCode().equals(record.getFolderFlag())){
            throw new GCloudBusinessException("文件夹暂不支持下载");
        }
        doPreview(context.getResponse(),record);
    }


    /**
     * 查询文件夹树列表
     * 1.查询出当前用户的全部的文件夹列表
     * 2.在内存中拼装文件夹树
     *
     * @param context 查询文件夹树上下文参数对象
     * @return
     */
    @Override
    public List<FolderTreeNodeVo> getFolderTree(QueryFolderTreeContext context) {
        List<GCloudUserFile> folderRecords = queryFolderRecords(context.getUserId());
        if(CollectionUtils.isEmpty(folderRecords)){
            return Lists.newArrayList();
        }
        return assembleFolderTreesNodeVo(folderRecords);
    }




    /********************************************** private方法 **********************************************/

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
     * 执行批量删除文件操作(放入回收站)
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


    /**
     * 合并文件并保存文件的实体记录
     *
     * @param context
     */
    private void mergeFileChunkAndSaveFileRecord(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConverter.FileChunkMergeContextToFileChunkMergeAndSaveContext(context);
        gCloudFileService.mergeFileChunkAndSaveFileRecord(fileChunkMergeAndSaveContext);
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }



    /**
     * 执行文件下载
     * 1.查询文件的真实存储路径
     * 2.添加跨域的公共响应头
     * 3.添加文件下载的属性信息
     * 4.委托文件存储引擎去读取文件中的内容到输出流中
     *
     * @param response
     * @param record
     */
    private void doDownload(HttpServletResponse response, GCloudUserFile record) {
        GCloudFile gCloudFile = gCloudFileService.getById(record.getFileId());
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(gCloudFile, response);
        realFileToOutputStream(gCloudFile.getRealPath(),response);
    }


    /**
     * 执行文件预览
     * 1.查询文件的真实存储路径
     * 2.添加跨域的公共响应头
     * 3.委托文件存储引擎去读取文件中的内容到输出流中
     *
     * @param response
     * @param record
     */
    private void doPreview(HttpServletResponse response,GCloudUserFile record) {
        GCloudFile gCloudFile = gCloudFileService.getById(record.getFileId());
        addCommonResponseHeader(response, gCloudFile.getFilePreviewContentType());
        realFileToOutputStream(gCloudFile.getRealPath(),response);
    }


    /**
     * 委托文件存储引擎去读取文件内容并写入到输出流中
     *
     * @param realPath
     * @param response
     */
    private void realFileToOutputStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext readFileContext = new ReadFileContext();
            readFileContext.setRealPath(realPath);
            readFileContext.setOutputStream(response.getOutputStream());
            storageEngine.readFile(readFileContext);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GCloudBusinessException("文件下载失败");
        }

    }


    /**
     * 添加文件下载的属性信息
     *
     * @param gCloudFile
     * @param response
     */
    private void addDownloadAttribute(GCloudFile gCloudFile, HttpServletResponse response){
        try {
            response.setHeader(FileConstants.CONTENT_DISPOSITION_STR,
                    FileConstants.CONTENT_DISPOSITION_VALUE_PREFIX_STR +
                            new String(gCloudFile.getFilename().getBytes(FileConstants.GB2312_STR),FileConstants.IOS_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new GCloudBusinessException("文件下载失败");
        }
        response.setContentLengthLong(Long.parseLong(gCloudFile.getFileSize()));
    }


    /**
     * 添加跨域的公共响应头
     *
     * @param response
     * @param contentTypeValue
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentTypeValue) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.setHeader(FileConstants.CONTENT_TYPE_STR, contentTypeValue);
        response.setContentType(contentTypeValue);
    }


    /**
     * 在内存中拼装文件夹树
     *
     * @param folderRecords
     * @return
     */
    private List<FolderTreeNodeVo> assembleFolderTreesNodeVo(List<GCloudUserFile> folderRecords) {
        List<FolderTreeNodeVo> mappedFolderTreeNodeVOList = folderRecords.stream()
                .map(fileConverter::GCloudUserFileToFolderTreeNodeVO)
                .collect(Collectors.toList());
        Map<Long, List<FolderTreeNodeVo>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream()
                .collect(Collectors.groupingBy(FolderTreeNodeVo::getParentId));
        for(FolderTreeNodeVo el: mappedFolderTreeNodeVOList){
            //获取的是el文件夹下的子文件夹集合
            List<FolderTreeNodeVo> children = mappedFolderTreeNodeVOMap.get(el.getId());
            if(CollectionUtils.isNotEmpty(children)){
                el.getChildren().addAll(children);
            }
        }
        //返回树形结构的parentId为0的根节点（可能有多个根节点，所以使用集合存储）
        return mappedFolderTreeNodeVOList.stream().
                filter(value->Objects.equals(value.getParentId(),FileConstants.TOP_PARENT_ID))
                .collect(Collectors.toList());
    }


    /**
     * 查询当前用户的全部文件夹列表
     *
     * @param userId
     * @return
     */
    private List<GCloudUserFile> queryFolderRecords(Long userId) {
        LambdaQueryWrapper<GCloudUserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUserFile::getUserId, userId)
                .eq(GCloudUserFile::getFolderFlag, FolderFlagEnum.YES.getCode())
                .eq(GCloudUserFile::getDelFlag, DelFlagEnum.NO.getCode());
        return list(lambdaQueryWrapper);
    }
}




