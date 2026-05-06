package com.zifang.z.config.web.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/actuator")
@Tag(name = "10000_监控端点")

public class BaseHealthController {

    @GetMapping("/health")
    @Operation(summary = "10000_健康检查")
    
    public String health() {
        return "UP";
    }
}