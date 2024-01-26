package com.grazy.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: grazy
 * @Date: 2024-01-26 20:40
 * @Description: Redis缓存配置类
 *  (该缓存方案支持事务)
 *  RedisTemplate 和 CacheManager 两种方式都是可以管理缓存数据
 */

@SpringBootConfiguration
@EnableCaching
@Slf4j
public class RedisCacheConfig {

    /**
     * 自定义连接和操作Redis的客户端工具(自己手动设置缓存层)
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //配置key的序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //配置value的序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //配置hashKey的序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //配置hashValue的序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }


    /**
     *  自定义Redis的缓存管理器 （将缓存交给Spring来管理--注解）
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class)));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .transactionAware()
                .build();

        log.info("The Redis Cache Manager is Loading Successfully!");

        return redisCacheManager;
    }
}
