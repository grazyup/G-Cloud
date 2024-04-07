package com.grazy.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Author: grazy
 * @Date: 2024-04-07 22:26
 * @Description: 布隆过滤器拦截器顶级接口
 */

public interface BloomFilterInterceptor extends HandlerInterceptor {

    /**
     * 获取布隆过滤器的拦截器名称
     *
     * @return
     */
    String getName();

    /**
     * 获取要拦截的URI的集合
     *
     * @return
     */
    String[] getPathPatterns();

    /**
     * 要排除拦截的URI的集合
     *
     * @return
     */
    String[] getExcludePatterns();
}
