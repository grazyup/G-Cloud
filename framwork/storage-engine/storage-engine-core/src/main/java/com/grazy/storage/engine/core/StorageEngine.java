package com.grazy.storage.engine.core;

import com.grazy.storage.engine.core.context.*;

import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2024-03-01 14:47
 * @Description: 文件存储引擎的顶级接口
 *  该接口定义所有需要向外暴露给业务层面的相关文件操作的功能
 *  业务方只能调用该接口的方法，而不能直接使用具体的实现方案去做业务调用
 */

public interface StorageEngine {

    /**
     * 存储物理文件
     *
     * @param context
     * @throws IOException
     */
    void store(StoreFileContext context) throws IOException;


    /**
     * 删除物理文件
     *
     * @param context
     * @throws IOException
     */
    void delete(DeleteStorageFileContext context) throws IOException;


    /**
     *  存储物理分片文件
     *
     * @param context
     * @throws IOException
     */
    void storeChunkFile(StoreChunkFileContext context) throws IOException;


    /**
     * 合并分片文件
     *
     * @param context
     * @throws IOException
     */
    void mergeFile(MergeFileContext context) throws IOException;


    /**
     * 读取文件内容写入到输出流中
     *
     * @param readFileContext
     * @throws IOException
     */
    void readFile(ReadFileContext readFileContext) throws IOException;
}
