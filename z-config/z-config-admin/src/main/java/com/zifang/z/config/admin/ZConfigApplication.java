package com.zifang.z.config.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zifang.z.config")
public class ZConfigApplication {

    private static Logger log = LoggerFactory.getLogger(ZConfigApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(ZConfigApplication.class, args);
        log.info("吃饭----");
        log.error("error");
    }

}
