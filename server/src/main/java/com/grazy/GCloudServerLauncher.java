package com.grazy;

import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * @Author: grazy
 * @Date: 2024-01-18 3:19
 * @Description: 项目启动类
 */

@SpringBootApplication(scanBasePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH)
@RestController
@Api("测试接口")
@Validated
@EnableTransactionManagement
@MapperScan(basePackages = GCloudConstants.BASE_COMPONENT_SCAN_PATH  + ".modules.**.mapper")
public class GCloudServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(GCloudServerLauncher.class);
    }

    @GetMapping("hello")
    public R<String> hello(@NotBlank(message = "name不能为空") String name) {
        return R.success("hello " + name + "!");
    }

}
