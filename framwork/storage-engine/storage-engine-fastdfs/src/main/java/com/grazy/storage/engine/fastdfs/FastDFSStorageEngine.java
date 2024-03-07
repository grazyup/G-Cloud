package com.grazy.storage.engine.fastdfs;

import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2024-03-01 15:06
 * @Description: FastDFS文件存储引擎实现类
 */

@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {

    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doDelete(DeleteStorageFileContext context) throws IOException {

    }

    @Override
    protected void doStoreChunkFile(StoreChunkFileContext context) throws IOException {

    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {

    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException{

    }
}
