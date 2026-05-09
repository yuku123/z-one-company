package com.zifang.z.agent.mcp.core;

import java.util.Map;

/**
 * 工具元数据（核心抽象，无 Lombok，无 Spring 依赖）
 */
public class ToolMeta {

    private String toolName;
    private String type;          // "BUILT_IN" or "THIRD_PARTY"
    private String description;
    private Map<String, Object> inputSchema;
    private Map<String, Object> outputSchema;
    private String executeUrl;    // 仅第三方工具
    private String authToken;     // 仅第三方工具
    private long createTime;

    public ToolMeta() {}

    // ── getters ──
    public String getToolName() { return toolName; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public Map<String, Object> getInputSchema() { return inputSchema; }
    public Map<String, Object> getOutputSchema() { return outputSchema; }
    public String getExecuteUrl() { return executeUrl; }
    public String getAuthToken() { return authToken; }
    public long getCreateTime() { return createTime; }

    // ── setters ──
    public void setToolName(String v) { this.toolName = v; }
    public void setType(String v) { this.type = v; }
    public void setDescription(String v) { this.description = v; }
    public void setInputSchema(Map<String, Object> v) { this.inputSchema = v; }
    public void setOutputSchema(Map<String, Object> v) { this.outputSchema = v; }
    public void setExecuteUrl(String v) { this.executeUrl = v; }
    public void setAuthToken(String v) { this.authToken = v; }
    public void setCreateTime(long v) { this.createTime = v; }
}
