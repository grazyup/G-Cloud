package com.grazy.storage.engine.local;

import com.grazy.core.utils.FileUtils;
import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.DeleteStorageFileContext;
import com.grazy.storage.engine.core.context.StoreFileContext;
import com.grazy.storage.engine.local.config.LocalStorageEngineConfigProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

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
}
