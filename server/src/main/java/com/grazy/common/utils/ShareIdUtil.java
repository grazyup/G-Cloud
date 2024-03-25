package com.grazy.common.utils;

import com.grazy.core.constants.GCloudConstants;

import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-26 0:16
 * @Description: 分享ID存储工具类
 */

public class ShareIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户id
     *
     * @param shareId
     */
    public static void set(Long shareId) {
        threadLocal.set(shareId);
    }


    /**
     * 获取当前线程的用户id
     *
     * @return
     */
    public static Long get() {
        Long shareId = threadLocal.get();
        if (Objects.isNull(shareId)) {
            return GCloudConstants.ZERO_LONG;
        }
        return shareId;
    }

}
