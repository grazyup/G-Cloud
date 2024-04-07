package com.grazy.bloom.filter.local.config;

import com.grazy.bloom.filter.local.enums.FunnelType;
import lombok.Data;

/**
 * @Author: grazy
 * @Date: 2024-04-07 14:51
 * @Description: 本地的布隆过滤器单体配置类
 */

@Data
public class LocalBloomFilterConfigItem {

    /**
     * 布隆过滤器名称
     */
    private String name;

    /**
     * 数据通道的名称
     */
    private String funnelTypeName = FunnelType.LONG.name();

    /**
     * 数组长度
     */
    private long expectedInsertions = 1000000L;

    /**
     * 误判率
     */
    private double fpp = 0.01D;
}
