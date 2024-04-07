package com.grazy.bloom.filter.local.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-04-07 14:49
 * @Description: 本地布隆过滤器配置
 */

@Component
@ConfigurationProperties(prefix = "com.grazy.bloom.filter.local")
@Data
public class LocalBloomFilterConfig {

    private List<LocalBloomFilterConfigItem> items;
}
