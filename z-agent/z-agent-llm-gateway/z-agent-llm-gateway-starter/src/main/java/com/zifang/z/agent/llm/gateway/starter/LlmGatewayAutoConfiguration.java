package com.zifang.z.agent.llm.gateway.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.zifang.z.agent.llm.gateway")
@MapperScan("com.zifang.z.agent.llm.gateway.mapper")
public class LlmGatewayAutoConfiguration {
}
