package com.grazy.bloom.filter.core;

import java.util.Collection;

/**
 * @Author: grazy
 * @Date: 2024-04-07 14:44
 * @Description: 布隆过滤器管理器顶级接口
 */

public interface BloomFilterManager<T> {

    /**
     * 根据名称获取对应的布隆过滤器
     *
     * @param name
     * @return
     */
    BloomFilter<T> getFilter(String name);


    /**
     * 获取目前管理中存在的布隆过滤器名称列表
     *
     * @return
     */
    Collection<String> getFilterNames();
}
