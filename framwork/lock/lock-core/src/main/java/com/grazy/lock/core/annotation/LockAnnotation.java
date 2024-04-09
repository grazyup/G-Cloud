package com.grazy.lock.core.annotation;

import com.grazy.lock.core.key.KeyGenerator;
import com.grazy.lock.core.key.StandardKeyGenerator;

import java.lang.annotation.*;

/**
 * @Author: grazy
 * @Date: 2024-04-09 9:50
 * @Description: 自定义锁注解
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LockAnnotation {

    /**
     * 锁名称
     *
     * @return
     */
    String name() default "";

    /**
     * 锁过期时间
     *
     * @return
     */
    long expireSecond() default 60L;

    /**
     * 自定义锁的key，支持el表达式
     *
     * @return
     */
    String[] keys() default {};

    /**
     * 指定的锁key的生成器
     *
     * @return
     */
    Class<? extends KeyGenerator> KeyGenerator() default StandardKeyGenerator.class;
}
