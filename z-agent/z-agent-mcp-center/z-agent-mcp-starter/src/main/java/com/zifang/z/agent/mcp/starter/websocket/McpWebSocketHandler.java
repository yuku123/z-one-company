package com.zifang.z.agent.mcp.starter.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zifang.z.agent.mcp.starter.McpRegistry;
import com.zifang.z.agent.mcp.starter.ToolMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP WebSocket 处理器
 * 实现 MCP 2024-11-05 协议的 WebSocket 传输
 *
 * ```
 *
 * ---
 *
 * ## 三、三种传输协议对比
 *
 * | 特性 | SSE | Streamable HTTP | WebSocket |
 * |------|-----|-----------------|-----------|
 * | **通信方式** | 服务器 → 客户端（单向） | 双向 | 双向（全双工） |
 * | **协议基础** | HTTP/1.1 | HTTP/1.1 或 HTTP/2 | TCP |
 * | **连接类型** | 长连接 | 可长可短 | 持久连接 |
 * | **数据格式** | text/event-stream | application/x-ndjson | Binary/Text |
 * | **自动重连** | 原生支持 | 需手动实现 | 需手动实现 |
 * | **防火墙穿透** | 良好 | 良好 | 一般 |
 * | **浏览器支持** | 原生支持 | 原生支持 | 原生支持 |
 * | **适用场景** | 服务器推送 | 流式请求/响应 | 实时双向通信 |
 *
 * ---
 *
 * ## 四、选择建议
 *
 * ### 4.1 选择 SSE 的场景
 *
 * - 主要需要服务器向客户端推送数据
 * - 需要简单的实现和良好的浏览器支持
 * - 不需要客户端向服务器频繁发送数据
 * - 需要通过防火墙或代理服务器
 *
 * ### 4.2 选择 Streamable HTTP 的场景
 *
 * - 需要流式请求和响应
 * - 需要处理大量数据
 * - 需要与现有的 HTTP 基础设施集成
 * - 需要简单的负载均衡和缓存
 *
 * ### 4.3 选择 WebSocket 的场景
 *
 * - 需要真正的双向实时通信
 * - 需要低延迟的消息传递
 * - 需要频繁的双向数据交换
 * - 不需要频繁地创建和关闭连接
 *
 * ---
 *
 * *文档结束*
 */
@Component
public class McpWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private McpRegistry registry;

    // 存储所有 WebSocket 会话
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);

        logger.info("WebSocket connection established: sessionId={}, remoteAddress={}",
                sessionId, session.getRemoteAddress());

        // 发送连接确认消息
        ObjectNode confirmation = objectMapper.createObjectNode();
        confirmation.put("jsonrpc", "2.0");
        confirmation.put("method", "connection/established");

        ObjectNode params = objectMapper.createObjectNode();
        params.put("sessionId", sessionId);
        params.put("timestamp", System.currentTimeMillis());
        confirmation.set("params", params);

        sendMessage(session, confirmation);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();

        logger.debug("Received WebSocket message: sessionId={}, payload={}", sessionId, payload);

        try {
            JsonNode request = objectMapper.readTree(payload);
            JsonNode response = processRequest(request, sessionId);

            if (response != null) {
                sendMessage(session, response);
            }
        } catch (Exception e) {
            logger.error("Error processing WebSocket message", e);
            sendErrorResponse(session, null, -32603, "Internal error: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);

        logger.info("WebSocket connection closed: sessionId={}, status={}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error: sessionId={}", session.getId(), exception);
    }

    /**
     * 处理请求
     */
    private JsonNode processRequest(JsonNode request, String sessionId) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");

        JsonNode idNode = request.get("id");
        if (idNode != null) {
            response.set("id", idNode);
        }

        String method = request.has("method") ? request.get("method").asText() : "";
        JsonNode params = request.get("params");

        switch (method) {
            case "initialize":
                handleInitialize(params, response);
                break;

            case "ping":
                handlePing(params, response);
                break;

            case "tools/list":
                handleToolsList(params, response);
                break;

            case "tools/call":
                handleToolsCall(params, response);
                break;

            case "resources/list":
                handleResourcesList(params, response);
                break;

            case "prompts/list":
                handlePromptsList(params, response);
                break;

            default:
                ObjectNode error = objectMapper.createObjectNode();
                error.put("code", -32601);
                error.put("message", "Method not found: " + method);
                response.set("error", error);
        }

        return response;
    }

    private void handleInitialize(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");

        ObjectNode serverInfo = objectMapper.createObjectNode();
        serverInfo.put("name", "z-agent-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.set("serverInfo", serverInfo);

        ObjectNode capabilities = objectMapper.createObjectNode();
        ObjectNode tools = objectMapper.createObjectNode();
        tools.put("listChanged", true);
        capabilities.set("tools", tools);
        result.set("capabilities", capabilities);

        response.set("result", result);
    }

    private void handlePing(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.put("timestamp", System.currentTimeMillis());
        response.set("result", result);
    }

    private void handleToolsList(JsonNode params, ObjectNode response) {
        List<ToolMeta> tools = registry.listTools(null);

        ArrayNode toolsArray = objectMapper.createArrayNode();
        for (ToolMeta tool : tools) {
            ObjectNode toolNode = objectMapper.createObjectNode();
            toolNode.put("name", tool.getToolName());
            toolNode.put("description", tool.getDescription());
            if (tool.getInputSchema() != null) {
                toolNode.set("inputSchema", objectMapper.valueToTree(tool.getInputSchema()));
            }
            toolsArray.add(toolNode);
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.set("tools", toolsArray);
        response.set("result", result);
    }

    private void handleToolsCall(JsonNode params, ObjectNode response) {
        if (params == null || !params.has("name")) {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("code", -32602);
            error.put("message", "Missing tool name");
            response.set("error", error);
            return;
        }

        String toolName = params.get("name").asText();

        ObjectNode result = objectMapper.createObjectNode();

        ArrayNode content = objectMapper.createArrayNode();
        ObjectNode textContent = objectMapper.createObjectNode();
        textContent.put("type", "text");
        textContent.put("text", "Tool " + toolName + " executed successfully");
        content.add(textContent);

        result.set("content", content);
        result.put("isError", false);

        response.set("result", result);
    }

    private void handleResourcesList(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.set("resources", objectMapper.createArrayNode());
        response.set("result", result);
    }

    private void handlePromptsList(JsonNode params, ObjectNode response) {
        ObjectNode result = objectMapper.createObjectNode();
        result.set("prompts", objectMapper.createArrayNode());
        response.set("result", result);
    }

    /**
     * 发送消息到指定会话
     */
    private void sendMessage(WebSocketSession session, JsonNode message) {
        try {
            session.sendMessage(new TextMessage(message.toString()));
        } catch (IOException e) {
            logger.error("Error sending WebSocket message", e);
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(WebSocketSession session, Object id, int code, String message) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        if (id != null) {
            response.put("id", id.toString());
        } else {
            response.putNull("id");
        }

        ObjectNode error = objectMapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);
        response.set("error", error);

        sendMessage(session, response);
    }

    /**
     * 向指定会话广播消息
     */
    public void broadcastMessage(Object message) {
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("Error serializing message", e);
            return;
        }

        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    logger.error("Error broadcasting message", e);
                }
            }
        }
    }
}
