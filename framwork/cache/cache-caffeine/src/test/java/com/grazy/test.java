package com.grazy;

import cn.hutool.core.lang.Assert;
import com.grazy.config.CaffeineCacheConfig;
import com.grazy.constants.CacheConstants;
import com.grazy.service.CacheAnnotationTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: grazy
 * @Date: 2024-01-26 2:56
 * @Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CaffeineCacheConfig.class)
public class test {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheAnnotationTester cacheAnnotationTester;

    /**
     * 测试缓存是否正常
     */
    @Test
    public void simpleCacheManagerTest() {
        Cache cache = cacheManager.getCache(CacheConstants.G_CLOUD_CACHE_NAME);
        Assert.notNull(cache);
        cache.put("key", "value");
        String value = cache.get("key", String.class);
        Assert.isTrue("value".equals(value));
    }

    /**
     * 测试注解是否正常
     */
    @Test
    public void simpleCacheAnnotationTest() {
        for (int i = 0; i < 2; i++) {
            cacheAnnotationTester.testCacheable("imooc");
        }
    }

}
