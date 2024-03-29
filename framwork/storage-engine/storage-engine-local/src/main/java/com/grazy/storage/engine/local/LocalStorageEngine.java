package com.grazy.storage.engine.local;

import com.grazy.core.utils.FileUtils;
import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.*;
import com.grazy.storage.engine.local.config.LocalStorageEngineConfigProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-01 14:56
 * @Description: 本地文件存储引擎实现类
 */

@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Resource
    private LocalStorageEngineConfigProperties localStorageEngineConfigProperties;


    /**
     * 保存物理文件
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String basePath = localStorageEngineConfigProperties.getRootFilePath();
        String realFilePath = FileUtils.generateStoreRealFilePath(basePath,context.getFilename());
        FileUtils.writeStreamToFile(context.getInputStream(), context.getTotalSize(), new File(realFilePath));
        context.setRealPath(realFilePath);
    }


    /**
     * 删除物理文件
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doDelete(DeleteStorageFileContext context) throws IOException {
        FileUtils.deleteFiles(context.getFileRealPathList());
    }


    /**
     * 保存物理分片文件
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStoreChunkFile(StoreChunkFileContext context) throws IOException {
        String basePath = localStorageEngineConfigProperties.getRootChunkFilePath();
        String realFilePath = FileUtils.generateStoreRealChunkFilePath(basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtils.writeStreamToFile(context.getInputStream(), context.getTotalSize(), new File(realFilePath));
        context.setRealPath(realFilePath);
    }


    /**
     * 合并分片文件
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        String basePath = localStorageEngineConfigProperties.getRootFilePath();
        String realFilePath = FileUtils.generateStoreRealFilePath(basePath,context.getFilename());
        FileUtils.createFile(new File(realFilePath));
        List<String> realPathList = context.getRealPathList();
        //文件追加读写
        for(String el: realPathList){
            FileUtils.appendWrite(Paths.get(realFilePath), new File(el).toPath());
        }
        //删除分片文件
        FileUtils.deleteFiles(realPathList);
        context.setRealPath(realFilePath);
    }


    /**
     * 读取文件内容并写入到输出流中
     *
     * @param context
     */
    @Override
    protected void doReadFile(ReadFileContext context) throws IOException{
        File file = new File(context.getRealPath());
        FileUtils.writeFileToOutputStream(new FileInputStream(file), context.getOutputStream(), file.length());
    }
}
