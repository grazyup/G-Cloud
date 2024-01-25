package com.grazy.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.grazy.constants.CacheConstants;
import com.grazy.core.constants.GCloudConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableCaching
@ComponentScan(value = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
public class CaffeineCacheConfig {

    @Autowired
    private CaffeineCacheProperties caffeineCacheProperties;

    @Bean
    public CacheManager caffeineManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheConstants.G_CLOUD_CACHE_NAME);
        cacheManager.setAllowNullValues(caffeineCacheProperties.getAllowNullValues());
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(caffeineCacheProperties.getInitCacheCapacity())
                .maximumSize(caffeineCacheProperties.getMaxCacheCapacity());
        cacheManager.setCaffeine(caffeineBuilder);
        return cacheManager;
    }

}