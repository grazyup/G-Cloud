package com.grazy.common.config;

import cn.hutool.core.collection.CollectionUtil;
import com.grazy.common.interceptor.BloomFilterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-04-07 22:44
 * @Description: 项目拦截器配置类
 */

@SpringBootConfiguration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private List<BloomFilterInterceptor> interceptorList;


    /**
     * 将项目中的全部布隆过滤器加载
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(CollectionUtil.isNotEmpty(interceptorList)){
            interceptorList.stream().forEach(bloomFilterInterceptor -> {
                registry.addInterceptor(bloomFilterInterceptor)
                        .addPathPatterns(bloomFilterInterceptor.getPathPatterns())
                        .excludePathPatterns(bloomFilterInterceptor.getExcludePatterns());
                log.info("add bloomFilterInterceptor {} finish.", bloomFilterInterceptor.getName());
            });
        }
    }
}
