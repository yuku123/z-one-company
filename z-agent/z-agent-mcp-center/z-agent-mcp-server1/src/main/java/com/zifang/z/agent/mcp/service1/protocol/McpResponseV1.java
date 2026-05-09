package com.zifang.z.agent.mcp.service1.protocol;

import lombok.Data;

/**
 * MCP 2024-11-05 标准响应格式
 */
@Data
public class McpResponseV1 {

    /**
     * JSON-RPC 版本
     */
    private String jsonrpc = "2.0";

    /**
     * 请求 ID（与请求对应）
     */
    private Object id;

    /**
     * 响应结果
     */
    private Object result;

    /**
     * 错误信息
     */
    private Error error;

    /**
     * Meta 信息
     */
    private ResponseMeta _meta;

    @Data
    public static class Error {
        /**
         * 错误码
         */
        private int code;

        /**
         * 错误消息
         */
        private String message;

        /**
         * 错误详情
         */
        private Object data;

        public static Error of(int code, String message) {
            Error error = new Error();
            error.setCode(code);
            error.setMessage(message);
            return error;
        }

        public static Error of(int code, String message, Object data) {
            Error error = new Error();
            error.setCode(code);
            error.setMessage(message);
            error.setData(data);
            return error;
        }
    }

    @Data
    public static class ResponseMeta {
        /**
         * 进度信息
         */
        private ProgressInfo progress;
    }

    @Data
    public static class ProgressInfo {
        /**
         * 进度值
         */
        private double progress;

        /**
         * 总进度
         */
        private double total;

        /**
         * 进度消息
         */
        private String message;
    }

    // ===== 便捷构造方法 =====

    public static McpResponseV1 success(Object id, Object result) {
        McpResponseV1 response = new McpResponseV1();
        response.setId(id);
        response.setResult(result);
        return response;
    }

    public static McpResponseV1 error(Object id, int code, String message) {
        McpResponseV1 response = new McpResponseV1();
        response.setId(id);
        response.setError(Error.of(code, message));
        return response;
    }

    public static McpResponseV1 error(Object id, int code, String message, Object data) {
        McpResponseV1 response = new McpResponseV1();
        response.setId(id);
        response.setError(Error.of(code, message, data));
        return response;
    }

    public static McpResponseV1 notification(String method, Object params) {
        McpResponseV1 response = new McpResponseV1();
        response.setId(null);  // 通知没有 id
        // 注意：通知通常使用不同的结构，这里简化处理
        return response;
    }
}
