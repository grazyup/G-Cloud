package com.grazy.web.cors;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2024-01-18 2:53
 * @Description: 跨域全局过滤器
 */

@WebFilter(filterName = "corsFilter")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        addCorsResponseHeader(response);
        filterChain.doFilter(servletRequest,servletResponse);
    }


    /**
     * 添加跨域的响应头
     *
     * @param response
     */
    private void addCorsResponseHeader(HttpServletResponse response) {
        response.setHeader(CorsConfigEnum.CORS_ORIGIN.getKey(), CorsConfigEnum.CORS_ORIGIN.getValue());
        response.setHeader(CorsConfigEnum.CORS_CREDENTIALS.getKey(), CorsConfigEnum.CORS_CREDENTIALS.getValue());
        response.setHeader(CorsConfigEnum.CORS_METHODS.getKey(), CorsConfigEnum.CORS_METHODS.getValue());
        response.setHeader(CorsConfigEnum.CORS_MAX_AGE.getKey(), CorsConfigEnum.CORS_MAX_AGE.getValue());
        response.setHeader(CorsConfigEnum.CORS_HEADERS.getKey(), CorsConfigEnum.CORS_HEADERS.getValue());
    }
}
