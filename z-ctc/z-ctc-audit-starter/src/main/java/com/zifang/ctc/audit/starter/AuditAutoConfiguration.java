package com.zifang.ctc.audit.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AuditAutoConfiguration {

    @Bean
    public AuditAspect auditAspect(AuditClient auditClient) {
        return new AuditAspect(auditClient);
    }
}
