package com.grazy;

import com.grazy.common.stream.channel.GCloudChannels;
import com.grazy.core.constants.GCloudConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: grazy
 * @Date: 2024-01-18 3:19
 * @Description: 项目启动类
 */

@SpringBootApplication(scanBasePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
@MapperScan(basePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH  + ".modules.**.mapper")
@Transactional
@EnableAsync
@EnableBinding(GCloudChannels.class)
public class GCloudServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(GCloudServerLauncher.class);
    }

}
