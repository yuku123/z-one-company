package com.zifang.z.meta.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * z-meta 管理后台启动类
 */
@SpringBootApplication(scanBasePackages = {"com.zifang.z.meta"})
@MapperScan("com.zifang.z.meta.core.mapper")
public class ZMetaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZMetaAdminApplication.class, args);
    }
}