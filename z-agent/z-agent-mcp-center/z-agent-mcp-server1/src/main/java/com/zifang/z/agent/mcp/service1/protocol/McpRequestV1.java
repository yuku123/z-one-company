package com.zifang.z.agent.mcp.service1.protocol;

import lombok.Data;

import java.util.Map;

/**
 * MCP 2024-11-05 标准请求格式
 */
@Data
public class McpRequestV1 {

    /**
     * JSON-RPC 版本
     */
    private String jsonrpc = "2.0";

    /**
     * 请求 ID
     */
    private Object id;

    /**
     * 方法名
     */
    private String method;

    /**
     * 请求参数
     */
    private Map<String, Object> params;

    /**
     * Meta 信息（用于进度通知等）
     */
    private RequestMeta _meta;

    @Data
    public static class RequestMeta {
        /**
         * 进度通知 token
         */
        private Object progressToken;
    }
}
