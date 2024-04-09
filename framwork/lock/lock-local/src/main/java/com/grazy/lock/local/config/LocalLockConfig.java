package com.grazy.lock.local.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * @Author: grazy
 * @Date: 2024-04-09 20:28
 * @Description: 本地锁配置类
 */

@SpringBootConfiguration
@Slf4j
public class LocalLockConfig {


    /**
     * 配置本地锁注册器
     *
     * @return
     */
    @Bean
    public LockRegistry LocalLockRegistry() {
        LockRegistry lockRegistry = new DefaultLockRegistry();
        log.info("the local lock is loaded successfully!");
        return lockRegistry;
    }
}
