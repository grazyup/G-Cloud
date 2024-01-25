package com.grazy.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-01-26 2:03
 * @Description: Caffeine配置属性实体类
 */

@Data
@Component
@ConfigurationProperties(prefix = "com.grazy.cache.caffeine")
public class CaffeineCacheConfigurationProperties {

    /**
     * Caffeine初始容量
     */
    private Integer initCacheCapacity;


    /**
     * 缓存最大存储容量，超过之后会按照 recently or very often（最近最小）策略做缓存剔除
     */
    private Long MaxCacheCapacity;


    /**
     * 是否允许空值value(缓存键值对中value是否为空)
     */
    private boolean allNullValue;

}
