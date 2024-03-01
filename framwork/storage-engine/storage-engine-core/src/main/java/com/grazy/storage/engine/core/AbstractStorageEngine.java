package com.grazy.storage.engine.core;

import com.grazy.constants.CacheConstants;
import com.grazy.core.exception.GCloudBusinessException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-01 14:48
 * @Description: 顶级文件存储引擎的公用父类
 */

public abstract class AbstractStorageEngine implements StorageEngine {

    @Resource
    private CacheManager cacheManager;


    /**
     * 公用获取缓存的方法
     *
     * @return
     */
    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new GCloudBusinessException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.G_CLOUD_CACHE_NAME);
    }

}
