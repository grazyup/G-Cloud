package com.grazy.common.cache;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-04-01 22:21
 * @Description: 支持业务缓存的顶级Service接口
 */

public interface CacheService<V> {

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    V getById(Serializable id);


    /**
     * 根据ID更新缓存信息
     *
     * @param id
     * @param entity
     * @return
     */
    boolean updateById(Serializable id, V entity);


    /**
     * 根据ID删除缓存信息
     *
     * @param id
     * @return
     */
    boolean removeById(Serializable id);

}
