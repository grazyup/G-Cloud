package com.grazy.caffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.grazy.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-01-26 2:11
 * @Description: Caffeine配置类
 */

@EnableCaching
@SpringBootConfiguration
@Slf4j
public class CaffeineCacheConfig {

    @Resource
    private CaffeineCacheConfigurationProperties caffeineCacheConfigurationProperties;

    @Bean
    public CacheManager caffeineManager(){
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(CacheConstants.G_CLOUD_CACHE_NAME);
        caffeineCacheManager.setAllowNullValues(caffeineCacheConfigurationProperties.isAllNullValue());
        Caffeine<Object,Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(caffeineCacheConfigurationProperties.getInitCacheCapacity())
                .maximumSize(caffeineCacheConfigurationProperties.getMaxCacheCapacity());
        caffeineCacheManager.setCaffeine(caffeineBuilder);
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE,"The Caffeine Cache Manager is Loading Successfully!"));
        return caffeineCacheManager;
    }

}
