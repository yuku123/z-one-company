package com.zifang.z.agent.mcp.service1.model;

import lombok.Data;

// MCP 标准响应
@Data
public class McpResponse {
    private String jsonrpc = "2.0";
    private String id;
    private Object result;
    private McpError error;
}
