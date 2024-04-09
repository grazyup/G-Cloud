package com.grazy.lock.zookeeper.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.zookeeper.config.CuratorFrameworkFactoryBean;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-04-10 0:13
 * @Description:
 */

@SpringBootConfiguration
@Slf4j
public class ZookeeperLockConfig {

    @Resource
    private ZookeeperLockConfigProperties properties;


    /**
     * 配置zk的客户端
     *
     * @return
     */
    @Bean
    public CuratorFrameworkFactoryBean curatorFrameworkFactoryBean() {
        return new CuratorFrameworkFactoryBean(properties.getHost());
    }


    /**
     * 配置zk分布式锁的注册器
     *
     * @return
     */
    @Bean
    public LockRegistry zookeeperLockRegistry(CuratorFramework curatorFramework) {
        ZookeeperLockRegistry zookeeperLockRegistry = new ZookeeperLockRegistry(curatorFramework);
        log.info("the zookeeper lock is loaded successfully!");
        return zookeeperLockRegistry;
    }
}
