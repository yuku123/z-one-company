package com.zifang.z.agent.llm.gateway.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.zifang.z.agent.llm.gateway")
public class WebConfig implements WebMvcConfigurer {
}
