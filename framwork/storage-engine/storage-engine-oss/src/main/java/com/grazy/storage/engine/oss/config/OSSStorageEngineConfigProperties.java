package com.grazy.storage.engine.oss.config;

import com.aliyun.oss.OSSClient;
import com.grazy.core.exception.GCloudBusinessException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-03-16 14:53
 * @Description: OSS文件存储引擎配置类
 */

@Data
@Component
@ConfigurationProperties(prefix = "com.grazy.storage.engine.oss")
public class OSSStorageEngineConfigProperties {

    /**
     * OSS对外服务的访问域名
     */
    private String endpoint;

    /**
     * 秘钥ID
     */
    private String accessKeyId;

    /**
     * 访问秘钥
     */
    private String accessKeySecret;

    /**
     * 存储空间名称（桶名称）
     */
    private String bucketName;

    /**
     * 是否自动创建桶
     */
    private boolean autoCreateBucket = Boolean.TRUE;


    /**
     * 注入OSS操作的客户端对象
     */
    @Bean(destroyMethod = "shutdown")
    public OSSClient ossClient(){
        if(StringUtils.isAnyBlank(getEndpoint(), getAccessKeyId(), getAccessKeySecret(), getBucketName())){
            throw new GCloudBusinessException("the oss config is missed!");
        }
        return new OSSClient(getEndpoint(),getAccessKeyId(),getAccessKeySecret());
    }

}
