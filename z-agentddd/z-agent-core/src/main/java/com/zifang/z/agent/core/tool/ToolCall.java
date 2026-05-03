package com.zifang.z.agent.core.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模型返回的工具调用指令（标准JSON结构）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolCall {
    private String toolName;            // 工具名
    private Map<String, Object> args;   // 工具参数
}