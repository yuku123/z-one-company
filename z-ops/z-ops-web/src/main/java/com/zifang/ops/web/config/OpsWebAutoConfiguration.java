package com.zifang.ops.web.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = "com.zifang.ops")
@MapperScan("com.zifang.ops.core.domain.mapper")
public class OpsWebAutoConfiguration {
}
