package com.zifang.z.agent.mcp.service1.protocol;

import lombok.Data;

import java.util.Map;

/**
 * MCP Tool 定义（符合 MCP 2024-11-05 协议）
 */
@Data
public class McpTool {

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 输入参数 JSON Schema
     */
    private Map<String, Object> inputSchema;

    /**
     * 输出参数 JSON Schema（可选）
     */
    private Map<String, Object> outputSchema;

    /**
     * 是否开启思考/推理模式
     */
    private boolean thinking;

    /**
     * 工具注解
     */
    private ToolAnnotations annotations;

    /**
     * 工具来源（用于区分内置/第三方）
     */
    private String source;

    /**
     * 执行端点（仅第三方工具）
     */
    private String executeEndpoint;

    @Data
    public static class ToolAnnotations {
        /**
         * 该工具是否只读（不会修改状态）
         */
        private boolean readOnlyHint;

        /**
         * 该工具是否幂等
         */
        private boolean idempotentHint;

        /**
         * 该工具是否打开 UI
         */
        private boolean openWorldHint;

        /**
         * 预计执行时间（秒）
         */
        private Double runTimeHint;

        /**
         * 该工具是否会导致破坏性操作
         */
        private boolean destructiveHint;
    }
}
