package com.zifang.z.agent.mcp.service1.model;

import lombok.Data;
import java.util.Map;

// MCP 标准请求
@Data
public class McpRequest {
    private String jsonrpc = "2.0";
    private String id;
    private String method;
    private Map<String, Object> params;
}

