package com.grazy.lock.core.key;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Maps;
import com.grazy.core.utils.SpElUtil;
import com.grazy.lock.core.annotation.LockAnnotation;
import com.grazy.lock.core.context.LockContext;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author: grazy
 * @Date: 2024-04-09 11:34
 * @Description: 锁的key生成器的公用父类
 */

public abstract class AbstractKeyGenerator implements KeyGenerator {

    /**
     * 生成锁的key
     *
     * @param lockContext
     * @return
     */
    @Override
    public String generateKey(LockContext lockContext) {
        LockAnnotation annotation = lockContext.getAnnotation();
        String[] keys = annotation.keys();
        HashMap<String, String> keyValueMap = Maps.newHashMap();
        if (ArrayUtil.isNotEmpty(keys)) {
            Arrays.stream(keys).forEach(key -> {
                keyValueMap.put(key, SpElUtil.getStringValue(key, lockContext.getClassName(), lockContext.getMethodName(), lockContext.getClassType(),
                        lockContext.getMethod(), lockContext.getArgs(), lockContext.getParameterTypes(), lockContext.getTarget()));
            });
        }
        return doGenerateKey(lockContext, keyValueMap);
    }


    /**
     * 下沉到子类中实现
     */
    protected abstract String doGenerateKey(LockContext lockContext, HashMap<String, String> keyValueMap);
}
