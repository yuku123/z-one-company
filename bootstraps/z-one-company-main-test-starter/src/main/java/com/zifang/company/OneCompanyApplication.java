package com.zifang.company;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 统一公司平台启动类
 * 整合所有核心服务：任务管理、工作流、网关等
 */
@SpringBootApplication(exclude = {
    TaskExecutionAutoConfiguration.class,
    TaskSchedulingAutoConfiguration.class
})
@ComponentScan(basePackages = {
    "com.zifang.z.one.company",
    "com.zifang.z.task.core"
})
@MapperScan({
    "com.zifang.z.one.company.mapper",
    "com.zifang.z.task.core.mapper"
})
public class OneCompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneCompanyApplication.class, args);
    }
}