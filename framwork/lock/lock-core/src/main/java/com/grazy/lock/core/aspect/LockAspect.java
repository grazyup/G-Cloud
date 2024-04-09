package com.grazy.lock.core.aspect;

import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.lock.core.context.LockContext;
import com.grazy.lock.core.key.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Author: grazy
 * @Date: 2024-04-09 12:54
 * @Description: 框架分布式锁统一切面增强逻辑实现
 */

@Component
@Aspect
@Slf4j
public class LockAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Resource
    private LockRegistry lockRegistry;


    @Pointcut(value = "@annotation(com.grazy.lock.core.annotation.LockAnnotation)")
    public void lockPointCut() {
    }


    /**
     * 环绕增强切面
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Around("lockPointCut()")
    public Object aroundLock(ProceedingJoinPoint proceedingJoinPoint) {
        LockContext lockContext = LockContext.init(proceedingJoinPoint);
        Lock lock = checkAndGetLock(lockContext);

        if (Objects.isNull(lock)) {
            log.error("the aspect get lock fail..");
            throw new GCloudBusinessException("aroundLock get lock fail");
        }

        Object result = null;
        boolean tryLockResult = false;
        try {
            tryLockResult = lock.tryLock(lockContext.getAnnotation().expireSecond(), TimeUnit.SECONDS);
            if (tryLockResult) {
                Object[] args = proceedingJoinPoint.getArgs();
                //携带目标方法的全部参数调用目标方法并携带其返回值返回
                result = proceedingJoinPoint.proceed(args);
            }
        } catch (InterruptedException e) {
            log.error("lock aspect tryLock exception.", e);
            throw new GCloudBusinessException("aroundLock tryLock fail.");
        } catch (Throwable e) {
            log.error("lock aspect tryLock exception.", e);
            throw new GCloudBusinessException("aroundLock tryLock fail.");
        } finally {
            if (tryLockResult) {
                lock.unlock();
            }
        }
        return result;
    }


    /**
     * 检查上下文的配置信息，返回锁实体
     *
     * @param lockContext
     * @return
     */
    private Lock checkAndGetLock(LockContext lockContext) {
        if (Objects.isNull(lockRegistry)) {
            log.error("the lockRegistry is not found...");
            return null;
        }
        String lockKey = getLockKey(lockContext);
        if (Strings.isBlank(lockKey)) {
            return null;
        }
        return lockRegistry.obtain(lockKey);
    }


    /**
     * 获取锁key的私有方法
     *
     * @param lockContext
     * @return
     */
    private String getLockKey(LockContext lockContext) {
        KeyGenerator keyGenerator = applicationContext.getBean(lockContext.getAnnotation().KeyGenerator());
        if(Objects.nonNull(keyGenerator)){
            return keyGenerator.generateKey(lockContext);
        }
        log.error("the keyGenerator is not found...");
        return StringUtils.EMPTY;
    }
}
