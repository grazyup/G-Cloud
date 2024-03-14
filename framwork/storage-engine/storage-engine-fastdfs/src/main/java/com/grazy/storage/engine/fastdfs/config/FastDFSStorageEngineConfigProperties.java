package com.grazy.storage.engine.fastdfs.config;

import com.github.tobato.fastdfs.conn.ConnectionPoolConfig;
import com.github.tobato.fastdfs.conn.FdfsConnectionPool;
import com.github.tobato.fastdfs.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.conn.TrackerConnectionManager;
import com.google.common.collect.Lists;
import com.grazy.core.exception.GCloudBusinessException;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-14 8:57
 * @Description: FastDFS 文件存储引擎配置类
 */

@SpringBootConfiguration
@Data
@ConfigurationProperties(prefix = "com.grazy.storage.engine.fdfs")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan(value = {"com.github.tobato.fastdfs.service", "com.github.tobato.fastdfs.domain"})
public class FastDFSStorageEngineConfigProperties {

    /**
     * 连接时间
     */
    private Integer connectTimeOut = 600;

    /**
     * 跟踪服务器地址列表
     */
    private List<String> trackerList = Lists.newArrayList();

    /**
     * 组名称
     */
    private String group = "group1";

    @Bean
    public PooledConnectionFactory pooledConnectionFactory(){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectTimeout(connectTimeOut);
        return pooledConnectionFactory;
    }

    @Bean
    public ConnectionPoolConfig connectionPoolConfig(){
        return new ConnectionPoolConfig();
    }

    @Bean
    public FdfsConnectionPool fdfsConnectionPool(ConnectionPoolConfig connectionPoolConfig, PooledConnectionFactory factory){
        return new FdfsConnectionPool(factory, connectionPoolConfig);
    }

    @Bean
    public TrackerConnectionManager trackerConnectionManager(FdfsConnectionPool fdfsConnectionPool){
        TrackerConnectionManager trackerConnectionManager = new TrackerConnectionManager();
        if(CollectionUtils.isEmpty(getTrackerList())){
            throw new GCloudBusinessException("the tracker list is empty");
        }
        trackerConnectionManager.setTrackerList(trackerList);
        return trackerConnectionManager;
    }


}
