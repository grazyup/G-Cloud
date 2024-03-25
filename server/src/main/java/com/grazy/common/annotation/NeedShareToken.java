package com.grazy.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: grazy
 * @Date: 2024-03-26 00:00
 * @Description: 该注解主要影响校验提取码通过后需要shareToken的接口
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedShareToken {

}
