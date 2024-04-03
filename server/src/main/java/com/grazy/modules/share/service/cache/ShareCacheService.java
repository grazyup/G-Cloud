package com.grazy.modules.share.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grazy.common.cache.AbstractManualCacheService;
import com.grazy.modules.share.domain.GCloudShare;
import com.grazy.modules.share.mapper.GCloudShareMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-04-03 14:15
 * @Description: 手动缓存实现分享业务的查询等操作
 */

@Component(value = "shareManualCacheService")
public class ShareCacheService extends AbstractManualCacheService<GCloudShare> {

    @Resource
    private GCloudShareMapper gCloudShareMapper;

    /**
     * 获取分享模块持久层mapper
     *
     * @return
     */
    @Override
    protected BaseMapper<GCloudShare> getBaseMapper() {
        return gCloudShareMapper;
    }


    /**
     * 获取缓存key的模版信息
     *
     * @return
     */
    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
