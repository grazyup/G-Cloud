package com.grazy.lock.redis.config;

import com.grazy.lock.core.constans.LockConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * @Author: grazy
 * @Date: 2024-04-09 22:09
 * @Description: Redis分布式锁配置类  --> 该方案集成spring-data-redis，配置项也复用原来的配置，不重复造轮子
 */

@SpringBootConfiguration
@Slf4j
public class RedisLockConfig {

    @Bean
    public LockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory){
        RedisLockRegistry redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, LockConstant.G_CLOUD_LOCK);
        log.info("redis lock is loaded successfully!");
        return redisLockRegistry;
    }
}
