package com.grazy.swagger2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-01-19 20:53
 * @Description: Swagger2配置属性实体类
 */

@Component
//自动装配配置文件中前缀为swagger2中的属性值，可省略@Value注解获取值
@ConfigurationProperties(prefix = "swagger2")
@Data
public class Swagger2ConfigProprieties {

    private boolean show;

    private String groupName;

    private String title;

    private String basePackage;

    private String description;

    private String termsOfServiceUrl;

    private String ContactName;

    private String ContactUrl;

    private String ContactEmail;

    private String Version;
}
