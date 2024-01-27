package com.grazy.config;

import com.grazy.core.constants.GCloudConstants;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author: grazy
 * @Date: 2024-01-27 19:35
 * @Description: 定时任务测试配置类
 */

@SpringBootConfiguration
@ComponentScan(GCloudConstants.BASE_COMPONENT_SCAN_PATH)
public class taskConfig {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

}
