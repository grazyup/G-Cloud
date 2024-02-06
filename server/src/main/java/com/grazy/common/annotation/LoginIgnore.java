package com.grazy.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: grazy
 * @Date: 2024-02-06 23:07
 * @Description: 该注解为不需要登录权限的接口，标注该注解的方法会自动屏蔽登录拦截校验逻辑
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginIgnore {

}
