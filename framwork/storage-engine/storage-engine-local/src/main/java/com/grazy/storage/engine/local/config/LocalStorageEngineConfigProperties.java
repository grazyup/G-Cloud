package com.grazy.storage.engine.local.config;

import com.grazy.core.utils.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-03-02 22:13
 * @Description: 本地文件存储配置属性
 */

@Component
@ConfigurationProperties(prefix = "com.grazy.storage.engine.local")
@Data
public class LocalStorageEngineConfigProperties {

    /**
     * 实际存放文件路径前缀
     */
    private String rootFilePath = FileUtils.generateDefaultStoreRealFilePath();

}
