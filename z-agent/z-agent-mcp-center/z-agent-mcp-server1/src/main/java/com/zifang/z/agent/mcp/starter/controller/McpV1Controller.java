package com.zifang.z.agent.mcp.starter.controller;

import com.zifang.z.agent.mcp.starter.protocol.*;
import com.zifang.z.agent.mcp.starter.service.McpProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP v1 标准协议控制器
 * 实现 MCP 2024-11-05 协议标准
 */
@RestController
@RequestMapping("/v1")
public class McpV1Controller {

    private static final Logger logger = LoggerFactory.getLogger(McpV1Controller.class);

    @Autowired
    private McpProtocolService protocolService;

    /**
     * 会话存储
     */
    private final Map<String, McpSession> sessions = new ConcurrentHashMap<>();

    /**
     * JSON-RPC 2.0 端点
     */
    @PostMapping(value = "/rpc", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<McpResponseV1> handleRpcRequest(
            @RequestBody McpRequestV1 request,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String sessionId,
            HttpServletRequest httpRequest) {

        logger.debug("RPC request: method={}, sessionId={}", request.getMethod(), sessionId);

        // 获取或创建会话
        McpSession session = getOrCreateSession(sessionId);

        // 处理请求
        McpResponseV1 response = protocolService.handleRequest(request, session);

        // 返回响应，包含 session ID
        return ResponseEntity.ok()
                .header("Mcp-Session-Id", session.getSessionId())
                .body(response);
    }

    /**
     * 批量请求端点（JSON-RPC 2.0 Batch）
     */
    @PostMapping(value = "/rpc/batch", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<McpResponseV1>> handleBatchRequest(
            @RequestBody List<McpRequestV1> requests,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String sessionId) {

        logger.debug("Batch request: count={}, sessionId={}", requests.size(), sessionId);

        McpSession session = getOrCreateSession(sessionId);
        List<McpResponseV1> responses = new ArrayList<>();

        for (McpRequestV1 request : requests) {
            McpResponseV1 response = protocolService.handleRequest(request, session);
            // 只添加有 ID 的响应（通知不需要响应）
            if (request.getId() != null) {
                responses.add(response);
            }
        }

        return ResponseEntity.ok()
                .header("Mcp-Session-Id", session.getSessionId())
                .body(responses);
    }

    /**
     * SSE 流式端点
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents(
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        String finalSessionId = sessionId != null ? sessionId : UUID.randomUUID().toString();
        logger.info("SSE stream requested: sessionId={}", finalSessionId);

        // 创建 SSE Emitter
        SseEmitter emitter = new SseEmitter(0L); // 无超时

        // 发送初始事件
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"sessionId\":\"" + finalSessionId + "\"}"));
        } catch (Exception e) {
            logger.error("Error sending initial SSE event", e);
        }

        // TODO: 存储 emitter 以便后续推送事件

        return emitter;
    }

    /**
     * 获取服务器能力
     */
    @GetMapping("/capabilities")
    public ResponseEntity<Map<String, Object>> getCapabilities() {
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("protocolVersion", McpProtocolConstants.PROTOCOL_VERSION);
        capabilities.put("serverInfo", createServerInfo());
        capabilities.put("capabilities", createCapabilities());

        return ResponseEntity.ok(capabilities);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");

        return ResponseEntity.ok(health);
    }

    // ==================== 私有方法 ====================

    private McpSession getOrCreateSession(String sessionId) {
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }

        final String finalSessionId = sessionId;
        return sessions.computeIfAbsent(sessionId, id -> {
            McpSession session = new McpSession();
            session.setSessionId(finalSessionId);
            session.setStatus(McpProtocolConstants.SESSION_STATUS_INITIALIZING);
            return session;
        });
    }

    private Map<String, Object> createServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "z-agent-mcp-server");
        info.put("version", "1.0.0");
        return info;
    }

    private Map<String, Object> createCapabilities() {
        Map<String, Object> caps = new HashMap<>();

        Map<String, Object> tools = new HashMap<>();
        tools.put("listChanged", true);
        caps.put("tools", tools);

        Map<String, Object> resources = new HashMap<>();
        resources.put("subscribe", true);
        resources.put("listChanged", true);
        caps.put("resources", resources);

        Map<String, Object> prompts = new HashMap<>();
        prompts.put("listChanged", true);
        caps.put("prompts", prompts);

        caps.put("logging", true);

        return caps;
    }
}
