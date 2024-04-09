package com.grazy.lock.zookeeper.instance;

import com.grazy.lock.core.annotation.LockAnnotation;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-04-09 20:32
 * @Description: 使用注解方式获取锁
 */

@Component
public class LockAnnotationTest {

    @LockAnnotation(name = "test", keys = "#name", expireSecond = 10L)
    public String testGetLock(String name){
        System.out.println(Thread.currentThread().getName() + " get the lock");
        String result = "hello" + name;
        System.out.println(Thread.currentThread().getName() + " release the lock");
        return result;
    }
}
