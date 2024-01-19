package com.grazy.swagger2;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: grazy
 * @Date: 2024-01-19 20:53
 * @Description: Swagger2配置属性实体类
 */

@Component
@ConfigurationProperties(prefix = "swagger2")
@Data
public class Swagger2ConfigProprieties {

    @Value("${swagger2.show}")
    private boolean show;

    @Value("${swagger2.group-name}")
    private String groupName;

    @Value("${swagger2.title}")
    private String title;

    @Value("${swagger2.base-package}")
    private String basePackage;

    @Value("${swagger2.description}")
    private String description;

    @Value("${swagger2.terms-of-service-url}")
    private String termsOfServiceUrl;

    @Value("${swagger2.contact-name}")
    private String ContactName;

    @Value("${swagger2.contact-url}")
    private String ContactUrl;

    @Value("${swagger2.contact-email}")
    private String ContactEmail;

    @Value("${swagger2.version}")
    private String Version;
}
