package com.zifang.company.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查和系统信息
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "z-one-company");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "One Company Platform");
        result.put("version", "1.0.0-SNAPSHOT");
        result.put("services", new String[]{"task", "workflow", "gateway"});
        return result;
    }
}