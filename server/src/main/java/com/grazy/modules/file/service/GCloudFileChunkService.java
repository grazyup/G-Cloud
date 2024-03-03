package com.grazy.modules.file.service;

import com.grazy.modules.file.context.FileChunkSaveContext;
import com.grazy.modules.file.domain.GCloudFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gaofu
* @description 针对表【g_cloud_file_chunk(文件分片信息表)】的数据库操作Service
* @createDate 2024-01-24 17:41:31
*/
public interface GCloudFileChunkService extends IService<GCloudFileChunk> {

    /**
     * 保存分片文件并保存已上传分片的记录
     *
     * @param fileChunkSaveContext 保存分片文件信息
     */
    void saveChunkFile(FileChunkSaveContext fileChunkSaveContext);
}
