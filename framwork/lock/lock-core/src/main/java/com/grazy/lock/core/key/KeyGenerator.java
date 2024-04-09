package com.grazy.lock.core.key;

import com.grazy.lock.core.context.LockContext;

/**
 * @Author: grazy
 * @Date: 2024-04-09 9:54
 * @Description: 锁的Key生成器顶级接口
 */

public interface KeyGenerator {

    /**
     * 生成锁的key
     *
     * @param lockContext
     * @return
     */
    String generateKey(LockContext lockContext);
}
