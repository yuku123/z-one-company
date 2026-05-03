package com.zifang.z.agent.mcp.starter;

import lombok.Data;

import java.util.Map;

@Data
public class ToolRegisterRequest {
    private String toolName;
    private String description;
    private Map<String, Object> inputSchema;
    private Map<String, Object> outputSchema;
    private String executeUrl;
    private String authToken;
}