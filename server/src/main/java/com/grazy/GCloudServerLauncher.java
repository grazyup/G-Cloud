package com.grazy;

import com.grazy.common.stream.channel.GCloudChannels;
import com.grazy.core.constants.GCloudConstants;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ConfigurableApplicationContext;
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
@Slf4j
public class GCloudServerLauncher {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(GCloudServerLauncher.class);
        printStartLog(applicationContext);
    }


    /**
     * 打印启动日志
     *
     * @param applicationContext
     */
    private static void printStartLog(ConfigurableApplicationContext applicationContext) {
            String serverPort = applicationContext.getEnvironment().getProperty("server.port");
            String serverUrl = String.format("http://%s:%s", "127.0.0.1", serverPort);
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE,"G-Cloud server started at:", serverUrl));
            if(checkShowServerDoc(applicationContext)){
                log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "G-Cloud server`s doc started at:", serverUrl + "/doc.html"));
            }
            log.info(AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, "G-Cloud server has started successfully!"));
    }


    /**
     * 校验是否开启接口文档
     * @param applicationContext
     * @return
     */
    private static boolean checkShowServerDoc(ConfigurableApplicationContext applicationContext) {
        return applicationContext.getEnvironment().getProperty("swagger2.show" ,Boolean.class,true)
                && applicationContext.containsBean("swagger2Config");
    }
}
