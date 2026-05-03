package com.zifang.z.agent.core.tool;


import lombok.Data;

import java.util.Map;

@Data
public class ToolCallRequest {
    // 工具名
    private String toolName;

    private Map<String, Object> parameters; // 工具参数
}

