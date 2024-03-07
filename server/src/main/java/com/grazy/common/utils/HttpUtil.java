package com.grazy.common.utils;

import com.grazy.web.cors.CorsConfigEnum;

import javax.servlet.http.HttpServletResponse;

/**
 * HTTP工具类
 */
public class HttpUtil {

    /**
     * 添加跨域的响应头
     *
     * @param response
     */
    public static void addCorsResponseHeaders(HttpServletResponse response) {
        for (CorsConfigEnum corsConfigEnum : CorsConfigEnum.values()) {
            response.setHeader(corsConfigEnum.getKey(), corsConfigEnum.getValue());
        }
    }
}