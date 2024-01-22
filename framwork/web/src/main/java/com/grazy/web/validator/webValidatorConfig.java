package com.grazy.web.validator;

import com.grazy.core.constants.GCloudConstants;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @Author: grazy
 * @Date: 2024-01-22 16:59
 * @Description: 全局统一参数校验器
 */

@SpringBootConfiguration
@Log4j2
public class webValidatorConfig {

    private static final String FAIL_FAST_KEY = "hibernate.validator.fail_fast";

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(GCloudValidator());
        log.info("The hibernate validator started successfully!");
        return methodValidationPostProcessor;
    }

    /**
     * 构造项目的方法参数校验器
     * @return
     */
    private Validator GCloudValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIL_FAST_KEY, GCloudConstants.TRUE_STR)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
