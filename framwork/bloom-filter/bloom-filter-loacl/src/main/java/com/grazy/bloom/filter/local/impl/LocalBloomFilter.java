package com.grazy.bloom.filter.local.impl;

import com.google.common.hash.Funnel;
import com.grazy.bloom.filter.core.BloomFilter;

/**
 * @Author: grazy
 * @Date: 2024-04-07 15:00
 * @Description: 本地布隆过滤器实现类
 */


public class LocalBloomFilter<T> implements BloomFilter<T> {

    /**
     * 谷歌提供的布隆过滤器实现委托
     */
    private com.google.common.hash.BloomFilter delegate;

    /**
     * 数据类型通道
     */
    private Funnel funnel;

    /**
     * 数组的长度
     */
    private long expectedInsertions;

    /**
     * 误判率
     */
    private double fpp;


    public LocalBloomFilter(Funnel funnel, long expectedInsertions, double fpp) {
        this.funnel = funnel;
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.delegate = com.google.common.hash.BloomFilter.create(funnel, expectedInsertions, fpp);
    }


    /**
     * 放入元素
     *
     * @param object
     * @return
     */
    @Override
    public boolean put(T object) {
        return delegate.put(object);
    }


    /**
     * 判断元素是否可能存在
     *
     * @param object
     * @return
     */
    @Override
    public boolean mightContain(T object) {
        return delegate.mightContain(object);
    }


    /**
     * 清空过滤器
     */
    @Override
    public void clear() {
        this.delegate = com.google.common.hash.BloomFilter.create(funnel, expectedInsertions, fpp);
    }
}
