package com.zifang.z.agent.mcp.starter;

import com.zifang.z.agent.mcp.core.McpRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP Starter 自动配置。
 *
 * 引入此 starter 后自动生效：
 * 1. 注册 McpRegistry Bean
 * 2. 注册 McpToolRegistrar BeanPostProcessor（扫描 @McpTool）
 * 3. 注册 McpAnnotationToolExecutor
 * 4. 注册 McpEndpointController（HTTP JSON-RPC 端点）
 */
@Configuration
public class McpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public McpRegistry mcpRegistry() {
        return new McpRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public McpAnnotationToolExecutor mcpAnnotationToolExecutor(McpRegistry registry) {
        return new McpAnnotationToolExecutor(registry);
    }

    @Bean
    public McpToolRegistrar mcpToolRegistrar(McpRegistry registry, McpAnnotationToolExecutor executor) {
        return new McpToolRegistrar(registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public McpEndpointController mcpEndpointController(McpRegistry registry, McpAnnotationToolExecutor executor) {
        return new McpEndpointController(registry, executor);
    }
}
