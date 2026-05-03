package com.zifang.z.agent.core.model;

import com.zifang.z.agent.core.model.define.ModelMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LlmCallerRequest {
    private String model; // 模型名（如 llama3:70b）
    private String format = "json"; // 强制返回 JSON
    private boolean stream = false; // 非流式
    private String toolChoice = "auto"; // 工具调用策略
    private List<ToolDefinition> tools; // 工具定义（与 messages 同级）
    private List<ModelMessage> modelMessages; // 对话消息列表

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ToolDefinition {
        private String type = "function"; // 固定为 function
        private FunctionDefinition function; // 函数详细定义
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FunctionDefinition {
        private String name; // 工具名
        private String description; // 工具描述
        private Object parameters; // 参数定义（JSON Schema）
    }
}