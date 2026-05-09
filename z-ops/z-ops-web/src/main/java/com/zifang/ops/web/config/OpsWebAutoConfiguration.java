package com.zifang.ops.web.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = "com.zifang.ops")
@MapperScan(basePackages = "com.zifang.ops.core.domain.mapper", sqlSessionFactoryRef = "sqlSessionFactoryConfig")
public class OpsWebAutoConfiguration {
}
