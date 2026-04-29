package com.zifang.z.wf.starter.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan("com.zifang.z.wf.core.domain.mapper")
@Configuration
public class MybatisPlusConfig {

}
