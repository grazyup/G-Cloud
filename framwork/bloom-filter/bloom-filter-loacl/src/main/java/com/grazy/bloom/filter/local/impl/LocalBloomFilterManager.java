package com.grazy.bloom.filter.local.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.grazy.bloom.filter.core.BloomFilter;
import com.grazy.bloom.filter.core.BloomFilterManager;
import com.grazy.bloom.filter.local.config.LocalBloomFilterConfig;
import com.grazy.bloom.filter.local.config.LocalBloomFilterConfigItem;
import com.grazy.bloom.filter.local.enums.FunnelType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: grazy
 * @Date: 2024-04-07 15:09
 * @Description: 布隆过滤器管理器实现类
 */

@Component
public class LocalBloomFilterManager implements BloomFilterManager, InitializingBean {

    @Resource
    private LocalBloomFilterConfig config;

    /**
     * 容器
     */
    private final Map<String,BloomFilter> bloomFilterContainer = Maps.newConcurrentMap();


    /**
     * 根据名称获取对应的布隆过滤器
     *
     * @param name
     * @return
     */
    @Override
    public BloomFilter getFilter(String name) {
        return bloomFilterContainer.get(name);
    }


    /**
     * 获取目前管理中存在的布隆过滤器名称列表
     *
     * @return
     */
    @Override
    public Collection<String> getFilterNames() {
        return bloomFilterContainer.keySet();
    }


    /**
     * 将配置文件中的过滤器信息初始化到容器中
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<LocalBloomFilterConfigItem> items = config.getItems();
        if(CollectionUtil.isNotEmpty(items)){
            items.stream().forEach(item -> {
                String funnelTypeName = item.getFunnelTypeName();
                try {
                    FunnelType funnelType = FunnelType.valueOf(funnelTypeName);
                    if(Objects.nonNull(funnelType)){
                        bloomFilterContainer.put(item.getName(),new LocalBloomFilter(funnelType.getFunnel(),item.getExpectedInsertions(),item.getFpp()));
                    }
                }catch (Exception e){

                }
            });
        }
    }
}
