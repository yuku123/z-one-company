package com.zifang.z.task.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Z-Task 任务管理平台启动类
 */
@SpringBootApplication(scanBasePackages = "com.zifang.z.task")
@MapperScan("com.zifang.z.task.core.mapper")
public class ZTaskStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZTaskStarterApplication.class, args);
    }
}
