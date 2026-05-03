package com.zifang.z.agent.mcp.starter.protocol;

/**
 * MCP 协议常量定义（符合 MCP 2024-11-05 协议）
 */
public class McpProtocolConstants {

    // ==================== 协议版本 ====================
    public static final String PROTOCOL_VERSION = "2024-11-05";
    public static final String JSON_RPC_VERSION = "2.0";

    // ==================== 端点路径 ====================
    public static final String ENDPOINT_MCP = "/mcp";
    public static final String ENDPOINT_MCP_V1 = "/v1/mcp";
    public static final String ENDPOINT_SSE = "/sse";
    public static final String ENDPOINT_STREAM = "/stream";
    public static final String ENDPOINT_REGISTRY = "/registry";

    // ==================== 标准方法名 (tools/) ====================
    public static final String METHOD_TOOLS_LIST = "tools/list";
    public static final String METHOD_TOOLS_CALL = "tools/call";

    // ==================== 标准方法名 (resources/) ====================
    public static final String METHOD_RESOURCES_LIST = "resources/list";
    public static final String METHOD_RESOURCES_READ = "resources/read";
    public static final String METHOD_RESOURCES_SUBSCRIBE = "resources/subscribe";
    public static final String METHOD_RESOURCES_UNSUBSCRIBE = "resources/unsubscribe";

    // ==================== 标准方法名 (prompts/) ====================
    public static final String METHOD_PROMPTS_LIST = "prompts/list";
    public static final String METHOD_PROMPTS_GET = "prompts/get";

    // ==================== 标准方法名 (sampling/) ====================
    public static final String METHOD_SAMPLING_CREATE_MESSAGE = "sampling/createMessage";

    // ==================== 标准方法名 (roots/) ====================
    public static final String METHOD_ROOTS_LIST = "roots/list";

    // ==================== 标准方法名 (utility/) ====================
    public static final String METHOD_INITIALIZE = "initialize";
    public static final String METHOD_PING = "ping";
    public static final String METHOD_NOTIFICATION_INITIALIZED = "notifications/initialized";
    public static final String METHOD_NOTIFICATION_CANCELLED = "notifications/cancelled";
    public static final String METHOD_NOTIFICATION_PROGRESS = "notifications/progress";
    public static final String METHOD_NOTIFICATION_RESOURCE_UPDATED = "notifications/resources/updated";
    public static final String METHOD_NOTIFICATION_RESOURCE_LIST_CHANGED = "notifications/resources/list_changed";
    public static final String METHOD_NOTIFICATION_TOOL_LIST_CHANGED = "notifications/tools/list_changed";
    public static final String METHOD_NOTIFICATION_PROMPT_LIST_CHANGED = "notifications/prompts/list_changed";

    // ==================== JSON-RPC 错误码 ====================
    public static final int ERROR_PARSE_ERROR = -32700;
    public static final int ERROR_INVALID_REQUEST = -32600;
    public static final int ERROR_METHOD_NOT_FOUND = -32601;
    public static final int ERROR_INVALID_PARAMS = -32602;
    public static final int ERROR_INTERNAL_ERROR = -32603;
    public static final int ERROR_SERVER_ERROR_START = -32000;
    public static final int ERROR_SERVER_ERROR_END = -32099;

    // ==================== 工具类型 ====================
    public static final String TOOL_TYPE_BUILT_IN = "BUILT_IN";
    public static final String TOOL_TYPE_THIRD_PARTY = "THIRD_PARTY";

    // ==================== HTTP 头 ====================
    public static final String HEADER_MCP_SESSION_ID = "Mcp-Session-Id";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_SSE = "text/event-stream";
    public static final String CONTENT_TYPE_JSONRPC = "application/jsonrpc+json";

    // ==================== 会话状态 ====================
    public static final String SESSION_STATUS_INITIALIZING = "initializing";
    public static final String SESSION_STATUS_ACTIVE = "active";
    public static final String SESSION_STATUS_CLOSING = "closing";
    public static final String SESSION_STATUS_CLOSED = "closed";

    private McpProtocolConstants() {
        // 工具类，禁止实例化
    }
}
