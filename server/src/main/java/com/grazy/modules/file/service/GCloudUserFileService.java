package com.grazy.modules.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grazy.modules.file.context.*;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.vo.GCloudUserFileVO;

import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-01-24 17:42:44
*/
public interface GCloudUserFileService extends IService<GCloudUserFile> {

    /**
     * 创建文件夹信息
     *
     * @param createFolderContext 文件夹上下文信息
     */
    Long createFolder(CreateFolderContext createFolderContext);


    /**
     * 查询用户的根文件夹信息
     *
     * @param userId 用户Id
     * @return 用户根文件夹信息实体
     */
    GCloudUserFile getFIleInfo(Long userId);


    /**
     * 查询用户的文件列表
     *
     * @param queryFileListContext 查询列表上下文信息
     * @return 文件列表数据Vo
     */
    List<GCloudUserFileVO> getFileList(QueryFileListContext queryFileListContext);


    /**
     * 文件重命名
     *
     * @param updateFilenameContext 重命名上下文信息
     */
    void updateFilename(UpdateFilenameContext updateFilenameContext);


    /**
     * 批量删除文件
     *
     * @param deleteFileContext 文件删除上下文信息
     */
    void deleteFile(DeleteFileContext deleteFileContext);


    /**
     * 文件秒传
     *
     * @param secUploadFileContext 文件秒传上下文信息
     * @return 秒传结果
     */
    boolean secUpload(SecUploadFileContext secUploadFileContext);
}
