package com.grazy.common.interceptor.impl;

import com.grazy.bloom.filter.core.BloomFilter;
import com.grazy.bloom.filter.core.BloomFilterManager;
import com.grazy.common.interceptor.BloomFilterInterceptor;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-04-07 22:29
 * @Description: 查询简单分享详情布隆过滤器拦截器
 */

@Component
@Slf4j
public class ShareSimpleDetailBloomFilterInterceptor implements BloomFilterInterceptor {

    @Resource
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";


    /**
     * 获取布隆过滤器的拦截器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "ShareSimpleDetailBloomFilterInterceptor";
    }


    /**
     * 获取要拦截的URI的集合
     *
     * @return
     */
    @Override
    public String[] getPathPatterns() {
        return ArrayUtils.toArray("/share/simple");
    }


    /**
     * 要排除拦截的URI的集合
     *
     * @return
     */
    @Override
    public String[] getExcludePatterns() {
        return new String[0];
    }


    /**
     * 返回true执行控制层方法，返回false拦截请求
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String encShareId = request.getParameter("shareId");
        if (Strings.isBlank(encShareId)) {
            throw new GCloudBusinessException("分享ID不能为空");
        }
        BloomFilter<Long> filter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(filter)) {
            log.info("the bloomFilter named {} is null, give up existence judgment...", BLOOM_FILTER_NAME);
            return true;
        }
        Long decrypt = IdUtil.decrypt(encShareId);
        boolean result = filter.mightContain(decrypt);
        if (result) {
            log.info("the bloomFilter named {} judge shareId {} mightContain pass...", BLOOM_FILTER_NAME, decrypt);
            return true;
        }
        log.info("the bloomFilter named {} judge shareId {} mightContain fail...", BLOOM_FILTER_NAME, decrypt);
        throw new GCloudBusinessException(ResponseCode.SHARE_CANCELLED);
    }
}
