package com.zifang.z.one.company.main.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        // A 包 → /a 前缀
//        configurer.addPathPrefix("/a", c -> c.getPackage().getName().startsWith("com.xxx.a"));
//        // B 包 → /b 前缀
//        configurer.addPathPrefix("/b", c -> c.getPackage().getName().startsWith("com.xxx.b"));
//    }
//}