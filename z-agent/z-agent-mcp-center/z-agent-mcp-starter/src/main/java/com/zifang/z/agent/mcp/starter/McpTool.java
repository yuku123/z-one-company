package com.zifang.z.agent.mcp.starter;

import java.lang.annotation.*;

/**
 * 标记一个方法为 MCP 工具。
 *
 * 使用方式:
 * <pre>{@code
 * @McpTool(name = "get_config", description = "获取配置值")
 * public String getConfig(@McpParam("key") String key) {
 *     return configService.get(key);
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McpTool {

    /** 工具名称（唯一标识，默认用方法名） */
    String name() default "";

    /** 工具描述 */
    String description() default "";
}
