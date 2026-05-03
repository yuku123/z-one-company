package com.zifang.z.agent.core.model.define;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelMessage {

    private String role; // user/assistant/tool/system

    private String content; // 消息内容

    private LocalDateTime timestamp; // 时间戳

    // 新增：Function Call 核心字段（OpenAI/Ollama 标准）
    private List<ToolCall> toolCalls;

    public ModelMessage(String role, String content) {
        this.role = role;
        this.content = StringUtils.trimToEmpty(content);
        this.timestamp = LocalDateTime.now();
    }

    public static ModelMessage of(String system, String string) {
        ModelMessage modelMessage = new ModelMessage();
        modelMessage.setRole(system);
        modelMessage.setContent(string);
        modelMessage.setTimestamp(LocalDateTime.now());
        return modelMessage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ToolCall {
        private String id; // 调用唯一标识（如 call_001）
        private String type; // 固定为 function_call
        private Function function; // 具体函数/工具调用信息
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Function {
        private String name; // 工具名（如 get_weather）
        private Object arguments; // 工具参数（JSON 对象，可解析为 Map）
    }
}