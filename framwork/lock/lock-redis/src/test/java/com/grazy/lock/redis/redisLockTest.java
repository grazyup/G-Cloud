package com.grazy.lock.redis;

import cn.hutool.core.lang.Assert;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.lock.core.constans.LockConstant;
import com.grazy.lock.redis.instance.LockAnnotationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Author: grazy
 * @Date: 2024-04-09 20:31
 * @Description: 本地锁测试
 */

@SpringBootTest(classes = redisLockTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication(scanBasePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH + ".lock")
@Slf4j
public class redisLockTest {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private LockRegistry lockRegistry;

    @Resource
    private LockAnnotationTest lockAnnotationTest;

    /**
     * 测试手动获取锁
     */
    @Test
    public void lockRegistryTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.execute(() -> {
                Lock lock = lockRegistry.obtain(LockConstant.G_CLOUD_LOCK);
                Assert.isTrue(Objects.nonNull(lock));
                boolean tryLock = false;
                try {
                    tryLock = lock.tryLock(10L, TimeUnit.SECONDS);
                    if(tryLock){
                        System.out.println(Thread.currentThread().getName() + " get the lock.");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if (tryLock){
                        System.out.println(Thread.currentThread().getName() + " release the lock.");
                        lock.unlock();
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }


    /**
     *  测试通过注解的方式获取锁
     */
    @Test
    public void lockAnnotationTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.execute(() -> {
                lockAnnotationTest.testGetLock("grazy");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
    }

}
