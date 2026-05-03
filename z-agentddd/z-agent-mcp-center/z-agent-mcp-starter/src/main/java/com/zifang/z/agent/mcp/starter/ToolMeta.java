package com.zifang.z.agent.mcp.starter;

import lombok.Data;

import java.util.Map;

/**
 * 工具元数据（内置+第三方）
 */
@Data
public class ToolMeta {
    // 工具名称（唯一键）
    private String toolName;
    // 类型：BUILT_IN/THIRD_PARTY
    private String type;
    // 工具描述
    private String description;
    // 入参Schema
    private Map<String, Object> inputSchema;
    // 出参Schema
    private Map<String, Object> outputSchema;
    // 第三方执行地址（仅THIRD_PARTY有效）
    private String executeUrl;
    // 第三方鉴权Token（仅THIRD_PARTY有效）
    private String authToken;
    // 注册/初始化时间
    private long createTime;
}