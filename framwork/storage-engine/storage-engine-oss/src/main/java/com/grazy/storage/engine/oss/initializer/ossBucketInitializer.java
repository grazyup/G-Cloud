package com.grazy.storage.engine.oss.initializer;

import com.aliyun.oss.OSSClient;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.storage.engine.oss.config.OSSStorageEngineConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-03-19 19:45
 * @Description: OSS桶的初始化
 */

@Component
@Slf4j
public class ossBucketInitializer implements CommandLineRunner {

    @Resource
    private OSSStorageEngineConfigProperties configProperties;

    @Resource
    private OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        boolean bucketExist = client.doesBucketExist(configProperties.getBucketName());
        if(!bucketExist && configProperties.isAutoCreateBucket()){
            //不存在，允许自动创建
            client.createBucket(configProperties.getBucketName());
        }
        if(!bucketExist && !configProperties.isAutoCreateBucket()){
            //不存在且不允许自动创建 -- 尽管此处不抛出异常，在真正使用过程中,也会因为没有找到指定的bucket导致oss自动抛出异常
            throw new GCloudBusinessException("the bucket " + configProperties.getBucketName() + " is not available");
        }
        log.info("the bucket " + configProperties.getBucketName() + " have been created!");
    }
}
