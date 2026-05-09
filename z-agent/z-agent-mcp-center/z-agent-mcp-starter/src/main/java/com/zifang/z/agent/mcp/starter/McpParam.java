package com.zifang.z.agent.mcp.starter;

import java.lang.annotation.*;

/**
 * 标记 MCP 工具方法的参数。
 *
 * 使用方式:
 * <pre>{@code
 * @McpTool(name = "get_config", description = "获取配置值")
 * public String getConfig(@McpParam(description = "配置的 key") String key) {
 *     ...
 * }
 * }</pre>
 *
 * 注意: 由于 Java 8 编译时不保留参数名，建议显式指定 name。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McpParam {

    /** 参数名（默认取方法参数名，但 Java 8 需要 -parameters 编译选项） */
    String name() default "";

    /** 参数描述 */
    String description() default "";

    /** 是否必填 */
    boolean required() default false;
}
