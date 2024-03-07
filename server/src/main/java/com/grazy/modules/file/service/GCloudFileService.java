package com.grazy.modules.file.service;

import com.grazy.modules.file.context.FileChunkMergeAndSaveContext;
import com.grazy.modules.file.context.FileChunkSaveContext;
import com.grazy.modules.file.context.FileSaveContext;
import com.grazy.modules.file.domain.GCloudFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gaofu
* @description 针对表【g_cloud_file(物理文件信息表)】的数据库操作Service
* @createDate 2024-01-24 17:41:31
*/
public interface GCloudFileService extends IService<GCloudFile> {

    /**
     * 上传文件并保存文件的实体记录
     *
     * @param fileSaveContext 文件保存上下文信息
     */
    void saveFile(FileSaveContext fileSaveContext);


    /**
     * 合并文件并保存文件的实体记录
     *
     * @param fileChunkMergeAndSaveContext 上下文参数
     */
    void mergeFileChunkAndSaveFileRecord(FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext);
}
