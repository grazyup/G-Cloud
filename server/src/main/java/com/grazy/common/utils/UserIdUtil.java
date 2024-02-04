package com.grazy.common.utils;

import com.grazy.core.constants.GCloudConstants;

import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-02-05 4:47
 * @Description: 用户ID存储工具类
 */

public class UserIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户id
     *
     * @param userId
     */
    public static void set(Long userId) {
        threadLocal.set(userId);
    }


    /**
     * 获取当前线程的用户id
     *
     * @return
     */
    public static Long get() {
        Long userId = threadLocal.get();
        if (Objects.isNull(userId)) {
            return GCloudConstants.ZERO_LONG;
        }
        return userId;
    }

}
