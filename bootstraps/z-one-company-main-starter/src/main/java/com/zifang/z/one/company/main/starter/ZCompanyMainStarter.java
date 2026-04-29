package com.zifang.z.one.company.main.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 临时禁用 SSO 认证
@SpringBootApplication(scanBasePackages = "com.zifang")
public class ZCompanyMainStarter {

    private static Logger log = LoggerFactory.getLogger(ZCompanyMainStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(ZCompanyMainStarter.class, args);
        log.info("启动成功");
    }

}