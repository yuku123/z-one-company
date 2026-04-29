package com.zifang.z.wf.starter;

import com.zifang.z.wf.starter.config.TokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zifang.z.wf")
public class ZWfApplication {
    private static Logger log = LoggerFactory.getLogger(TokenContext.class);

    public static void main(String[] args) {



        SpringApplication.run(ZWfApplication.class, args);
        log.info("吃饭----");
        log.error("error");
    }

}
