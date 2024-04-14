package com.grazy.modules.user.service.cache;

import com.grazy.common.cache.AnnotationCacheService;
import com.grazy.constants.CacheConstants;
import com.grazy.modules.user.domain.GCloudUser;
import com.grazy.modules.user.mapper.GCloudUserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-04-03 14:20
 * @Description: 用户模块缓存业务处理类
 */

@Component(value = "userAnnotationCacheService")
public class UserCacheService implements AnnotationCacheService<GCloudUser> {

    @Resource
    private GCloudUserMapper gCloudUserMapper;


    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.G_CLOUD_CACHE_NAME,keyGenerator = "userIdKeyGenerator", sync = true) //sync类似加锁一样
    @Override
    public GCloudUser getById(Serializable id) {
        return gCloudUserMapper.selectById(id);
    }


    /**
     * 根据ID来更新缓存信息
     *
     * @param id
     * @param entity
     * @return
     */
    @CacheEvict(cacheNames = CacheConstants.G_CLOUD_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, GCloudUser entity) {
        return gCloudUserMapper.updateById(entity) == 1;
    }


    /**
     * 根据ID来删除缓存信息
     *
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = CacheConstants.G_CLOUD_CACHE_NAME,keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return gCloudUserMapper.deleteById(id) == 1;
    }
}
