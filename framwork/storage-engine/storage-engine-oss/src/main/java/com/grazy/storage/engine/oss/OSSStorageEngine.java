package com.grazy.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSSClient;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.utils.FileUtils;
import com.grazy.core.utils.UUIDUtil;
import com.grazy.storage.engine.core.AbstractStorageEngine;
import com.grazy.storage.engine.core.context.*;
import com.grazy.storage.engine.oss.config.OSSStorageEngineConfigProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2024-03-01 15:00
 * @Description: OSS文件存储引擎实现类
 */

@Component
public class OSSStorageEngine extends AbstractStorageEngine {

    @Resource
    private OSSStorageEngineConfigProperties configProperties;

    @Resource
    private OSSClient ossClient;

    /**
     * 单文件上传
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String realPath = getFilePath(FileUtils.getFileSuffix(context.getFilename()));
        ossClient.putObject(configProperties.getBucketName(),realPath,context.getInputStream());
        context.setRealPath(realPath);
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



    /********************************************** private方法 **********************************************/

    /**
     * 获取对象的完整名称（ObjectName、key、ObjectKey）
     * @param fileSuffix
     * @return
     */
    private String getFilePath(String fileSuffix) {
        return new StringBuffer()
                .append(DateUtil.thisYear())
                .append(GCloudConstants.SLASH_STR)
                .append(DateUtil.thisMonth() + 1)
                .append(GCloudConstants.SLASH_STR)
                .append(DateUtil.thisDayOfMonth())
                .append(GCloudConstants.SLASH_STR)
                .append(UUIDUtil.getUUID())
                .append(fileSuffix)
                .toString();
    }
}
