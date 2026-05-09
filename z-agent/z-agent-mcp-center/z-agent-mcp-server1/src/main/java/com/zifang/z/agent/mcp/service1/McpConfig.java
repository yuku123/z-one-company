package com.zifang.z.agent.mcp.service1;

import com.zifang.z.agent.mcp.core.McpRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * server1 模块的 Spring 配置：将 core 的纯 Java 类注册为 Spring Bean
 */
@Configuration
public class McpConfig {

    @Bean
    public McpRegistry mcpRegistry() {
        return new McpRegistry();
    }
}
