package com.grazy.swagger2;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-01-19 21:01
 * @Description: Swagger2配置文件
 */

@SpringBootConfiguration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Slf4j
public class Swagger2Config {

    @Resource
    private Swagger2ConfigProprieties configProprieties;

    @Bean
    public Docket OrderRestApi(){
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .enable(configProprieties.isShow())
                .groupName(configProprieties.getGroupName())
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage(configProprieties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
        log.info("The Swagger2 have been loading successful!");
        return docket;
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(configProprieties.getTitle())
                .description(configProprieties.getDescription())
                .termsOfServiceUrl(configProprieties.getTermsOfServiceUrl())
                .contact(new Contact(configProprieties.getContactName(), configProprieties.getContactUrl(), configProprieties.getContactEmail()))
                .version(configProprieties.getVersion())
                .build();
    }


}
