package com.grazy;

import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: grazy
 * @Date: 2024-01-18 3:19
 * @Description: 项目启动类
 */

@SpringBootApplication(scanBasePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
public class GCloudServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(GCloudServerLauncher.class);
    }


    @GetMapping("hello")
    public R<String> hello(String name) {
        return R.success("hello " + name + "!");
    }

}
