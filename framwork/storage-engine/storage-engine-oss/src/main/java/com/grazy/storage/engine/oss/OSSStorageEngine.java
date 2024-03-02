package com.grazy.storage.engine.oss;

import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.DeleteStorageFileContext;
import com.grazy.storage.engine.core.context.StoreFileContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2024-03-01 15:00
 * @Description: OSS文件存储引擎实现类
 */

@Component
public class OSSStorageEngine extends AbstractStorageEngine {
    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doDelete(DeleteStorageFileContext context) throws IOException {

    }
}
