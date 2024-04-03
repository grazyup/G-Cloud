package com.grazy.common.config;

import com.grazy.core.constants.GCloudConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-03-03 21:17
 * @Description: GCloud服务层配置属性
 */

@Component
@ConfigurationProperties(prefix = "com.grazy.server")
@Data
public class GCloudServerConfigProperties {

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = GCloudConstants.ONE_INT;


    /**
     * 文件分享链接URL前缀
     */
    private String sharePrefix = "http://127.0.0.1:8080" + "share/";

}
