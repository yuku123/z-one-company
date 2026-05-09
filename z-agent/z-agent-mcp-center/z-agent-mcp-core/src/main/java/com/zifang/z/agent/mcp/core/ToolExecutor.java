package com.zifang.z.agent.mcp.core;

import java.util.Map;

/**
 * 工具执行器接口
 */
public interface ToolExecutor {

    /**
     * 执行工具，返回 MCP 标准格式
     *
     * @param toolName  工具名
     * @param arguments 调用参数
     * @return {"content": [{"type":"text", "text":"..."}], "isError": false}
     */
    Map<String, Object> execute(String toolName, Map<String, Object> arguments);

    /**
     * 判断是否支持该工具
     */
    boolean supports(String toolName);
}
