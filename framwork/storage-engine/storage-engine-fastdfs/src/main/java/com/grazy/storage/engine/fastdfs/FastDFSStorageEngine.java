package com.grazy.storage.engine.fastdfs;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.FileUtils;
import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.*;
import com.grazy.storage.engine.fastdfs.config.FastDFSStorageEngineConfigProperties;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-01 15:06
 * @Description: FastDFS文件存储引擎实现类
 */

@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FastDFSStorageEngineConfigProperties configProperties;

    /**
     * 执行保存物理文件(单文件上传)
     *
     * @param context
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(configProperties.getGroup(), context.getInputStream()
                , context.getTotalSize(), FileUtils.getFileSuffixNotWithPoint(context.getFilename()));
        context.setRealPath(storePath.getFullPath());
    }


    /**
     * 执行删除物理文件
     *
     * @param context
     */
    @Override
    protected void doDelete(DeleteStorageFileContext context) throws IOException {
        List<String> fileRealPathList = context.getFileRealPathList();
        if(CollectionUtils.isNotEmpty(fileRealPathList)){
            fileRealPathList.stream().forEach(fastFileStorageClient::deleteFile);
        }
    }


    /**
     * 执行保存物理分片文件（上传分片）
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStoreChunkFile(StoreChunkFileContext context) throws IOException {
        throw new GCloudBusinessException("FastDFS不提供现成的分片上传功能");
    }


    /**
     * 执行合并分片文件
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        throw new GCloudBusinessException(("FastDFS不提供现成的合并分片功能"));
    }


    /**
     * 执行文件内容读取操作
     *
     * @param context
     */
    @Override
    protected void doReadFile(ReadFileContext context) throws IOException{
        String realPath = context.getRealPath();
        String group = realPath.substring(GCloudConstants.ZERO_INT, realPath.indexOf(GCloudConstants.SLASH_STR));
        String path = realPath.substring(realPath.indexOf(GCloudConstants.SLASH_STR) + GCloudConstants.ZERO_INT);
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, new DownloadByteArray());
        OutputStream outputStream = context.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
