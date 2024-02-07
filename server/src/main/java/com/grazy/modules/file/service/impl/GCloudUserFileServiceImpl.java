package com.grazy.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.enums.FolderFlagEnum;
import com.grazy.modules.file.mapper.GCloudUserFileMapper;
import com.grazy.modules.file.service.GCloudUserFileService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author gaofu
 * @description 针对表【g_cloud_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-01-24 17:42:44
 */

@Service(value = "GCloudUserFileService")
public class GCloudUserFileServiceImpl extends ServiceImpl<GCloudUserFileMapper, GCloudUserFile> implements GCloudUserFileService {

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


    /************************************* 通用创建用户文件/文件夹方法 *************************************/

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
}




