package com.grazy.web.cors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: grazy
 * @Date: 2024-01-18 2:56
 * @Description: 跨域设置枚举类
 */

@AllArgsConstructor
@Getter
public enum CorsConfigEnum {
    /**
     * 允许所有远程访问
     */
    CORS_ORIGIN("Access-Control-Allow-Origin", "*"),
    /**
     * 允许认证
     */
    CORS_CREDENTIALS("Access-Control-Allow-Credentials", "true"),
    /**
     * 允许远程调用的请求类型
     */
    CORS_METHODS("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT"),
    /**
     * 指定本次预检请求的有效期，单位是秒
     */
    CORS_MAX_AGE("Access-Control-Max-Age", "3600"),
    /**
     * 允许所有请求头
     */
    CORS_HEADERS("Access-Control-Allow-Headers", "*");

    private String key;

    private String value;

}
