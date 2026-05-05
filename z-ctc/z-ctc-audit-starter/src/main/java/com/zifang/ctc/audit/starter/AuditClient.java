package com.zifang.ctc.audit.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
public class AuditClient {

    private static final Logger log = LoggerFactory.getLogger(AuditClient.class);

    @Value("${audit.ctc.url:http://localhost:8080}")
    private String ctcUrl;

    @Value("${audit.application:unknown}")
    private String application;

    @Value("${audit.enabled:true}")
    private boolean enabled;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    @Async
    public void send(AuditEvent event) {
        if (!enabled) return;
        try {
            event.setApplication(application);
            event.setTimestamp(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now());
            String url = ctcUrl + "/api/audit/event";
            restTemplate.postForEntity(url, event, Void.class);
            log.debug("审计事件已上报: {} {} {}", event.getOperationType(), event.getOperationDesc(), event.getRequestUrl());
        } catch (Exception e) {
            log.error("审计事件上报失败: {}", e.getMessage());
        }
    }
}
