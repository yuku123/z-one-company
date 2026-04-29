package com.zifang.z.wf.starter.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Web配置：注册拦截器
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns( // 排除公开接口（与isPublicUrl逻辑一致，双重保障）
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/public/**",
                        "/swagger-ui/**", // 排除Swagger文档（开发环境）
                        "/v3/api-docs/**"
                );
    }
}