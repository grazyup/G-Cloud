package com.grazy.lock.zookeeper.config;

import com.grazy.lock.core.constans.LockConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-04-10 0:14
 * @Description: zookeeper配置属性
 */

@Data
@Component
@ConfigurationProperties(prefix = "com.grazy.lock.zookeeper")
public class ZookeeperLockConfigProperties {

    /**
     * zk链接地址，多个使用逗号隔开
     */
    private String host = "127.0.0.1:2181";

    /**
     * zk分布式锁的根路径
     */
    private String rootPath = LockConstant.G_CLOUD_LOCK_PATH;

}
