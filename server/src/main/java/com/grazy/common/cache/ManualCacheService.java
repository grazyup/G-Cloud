package com.grazy.common.cache;

import org.springframework.cache.Cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: grazy
 * @Date: 2024-04-01 22:28
 * @Description: 手动缓存处理Service顶级接口
 *              (手动处理缓存较灵活，可以批量处理缓存,所以加上批量处理缓存方法)
 */

public interface ManualCacheService<V> extends CacheService<V> {

    /**
     * 根据ID集合查询实体记录列表
     *
     * @param ids
     * @return
     */
    List<V> getByIds(Collection<? extends Serializable> ids);


    /**
     * 批量更新实体记录
     *
     * @param entityMap
     * @return
     */
    boolean updateByIds(Map<? extends Serializable, V> entityMap);


    /**
     * 批量删除实体记录
     *
     * @param ids
     * @return
     */
    boolean removeByIds(Collection<? extends Serializable> ids);


    /**
     * 获取缓存key的模版信息
     *
     * @return
     */
    String getKeyFormat();


    /**
     * 获取缓存对象实体
     *
     * @return
     */
    Cache getCache();
}
