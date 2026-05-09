package com.zifang.z.agent.mcp.starter.protocol;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * MCP Prompt 定义（符合 MCP 2024-11-05 协议）
 */
@Data
public class McpPrompt {

    /**
     * Prompt 名称
     */
    private String name;

    /**
     * Prompt 描述
     */
    private String description;

    /**
     * Prompt 参数定义
     */
    private PromptArgument arguments;

    /**
     * Prompt 消息列表（用于预定义对话）
     */
    private List<PromptMessage> messages;

    /**
     * Prompt 注解
     */
    private Map<String, Object> annotations;

    @Data
    public static class PromptArgument {
        /**
         * 参数列表
         */
        private List<Argument> properties;

        /**
         * 必需参数列表
         */
        private List<String> required;
    }

    @Data
    public static class Argument {
        /**
         * 参数名
         */
        private String name;

        /**
         * 参数描述
         */
        private String description;

        /**
         * 参数类型
         */
        private String type;

        /**
         * 默认值
         */
        private Object defaultValue;

        /**
         * 枚举值
         */
        private List<String> enumValues;
    }

    @Data
    public static class PromptMessage {
        /**
         * 角色：system, user, assistant
         */
        private String role;

        /**
         * 消息内容
         */
        private McpContent content;

        /**
         * 工具调用信息（当 role=assistant 且有工具调用时）
         */
        private ToolCall toolCalls;

        /**
         * 工具调用结果（当 role=tool 时）
         */
        private ToolCallResult toolCallResult;

        @Data
        public static class ToolCall {
            /**
             * 工具调用 ID
             */
            private String id;

            /**
             * 工具名称
             */
            private String name;

            /**
             * 工具参数
             */
            private Map<String, Object> arguments;
        }

        @Data
        public static class ToolCallResult {
            /**
             * 工具调用 ID
             */
            private String id;

            /**
             * 工具输出
             */
            private Object output;

            /**
             * 是否出错
             */
            private boolean isError;
        }
    }
}
