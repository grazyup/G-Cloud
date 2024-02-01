package com.grazy.modules.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.domain.GCloudUserFile;

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
}
