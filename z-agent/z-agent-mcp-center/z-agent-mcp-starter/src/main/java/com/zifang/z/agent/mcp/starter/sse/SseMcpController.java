package com.zifang.z.agent.mcp.starter.sse;

import com.zifang.z.agent.mcp.starter.protocol.*;
import com.zifang.z.agent.mcp.starter.service.McpProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP SSE 流式传输控制器
 * 支持 MCP 2024-11-05 协议的流式通信
 */
@RestController
@RequestMapping("/mcp")
public class SseMcpController {

    private static final Logger logger = LoggerFactory.getLogger(SseMcpController.class);

    @Autowired
    private McpProtocolService protocolService;

    private final SseEmitterManager sseManager = new SseEmitterManager();

    /**
     * 会话存储：sessionId -> session
     */
    private final Map<String, McpSession> sessions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("SSE MCP Controller initialized");
    }

    @PreDestroy
    public void destroy() {
        sseManager.shutdown();
        sessions.clear();
    }

    /**
     * SSE 端点：客户端连接此端点接收流式消息
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(
            @RequestParam(required = false) String sessionId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId,
            HttpServletRequest request) {

        // 优先从 header 获取，其次是 query param，最后生成新的
        String finalSessionId = headerSessionId != null ? headerSessionId :
                (sessionId != null ? sessionId : UUID.randomUUID().toString());

        logger.info("SSE connection request: sessionId={}, client={}",
                finalSessionId, request.getRemoteAddr());

        // 创建或获取会话
        McpSession session = sessions.computeIfAbsent(finalSessionId, id -> {
            McpSession newSession = new McpSession();
            newSession.setSessionId(id);
            newSession.setStatus(McpProtocolConstants.SESSION_STATUS_INITIALIZING);
            return newSession;
        });

        // 创建 SSE Emitter
        SseEmitter emitter = sseManager.createEmitter(finalSessionId, 0L); // 0 = 无超时

        // 发送初始事件
        try {
            emitter.send(SseEmitter.event()
                    .name("endpoint")
                    .data("/mcp/message?sessionId=" + finalSessionId));

            emitter.send(SseEmitter.event()
                    .name("sessionId")
                    .data(finalSessionId));
        } catch (IOException e) {
            logger.error("Error sending initial SSE events", e);
        }

        return emitter;
    }

    /**
     * 消息端点：客户端 POST 消息到此端点
     */
    @PostMapping(value = "/message", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<McpResponseV1> handleMessage(
            @RequestBody McpRequestV1 request,
            @RequestParam String sessionId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId) {

        String finalSessionId = headerSessionId != null ? headerSessionId : sessionId;

        logger.debug("Received message: sessionId={}, method={}", finalSessionId, request.getMethod());

        // 获取或创建会话
        McpSession session = sessions.get(finalSessionId);
        if (session == null) {
            return ResponseEntity.badRequest()
                    .body(McpResponseV1.error(request.getId(),
                            McpProtocolConstants.ERROR_INVALID_PARAMS,
                            "Session not found: " + finalSessionId));
        }

        // 更新会话活动
        session.updateActivity();

        // 处理请求
        McpResponseV1 response = protocolService.handleRequest(request, session);

        // 如果是初始化请求，更新会话状态
        if ("initialize".equals(request.getMethod()) && response.getError() == null) {
            session.setStatus(McpProtocolConstants.SESSION_STATUS_ACTIVE);
            session.setProtocolVersion(extractProtocolVersion(request));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 流式调用端点：支持流式响应
     */
    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleStream(
            @RequestBody McpRequestV1 request,
            @RequestParam(required = false) String sessionId,
            @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId) {

        String finalSessionId = headerSessionId != null ? headerSessionId :
                (sessionId != null ? sessionId : UUID.randomUUID().toString());

        // 创建 SSE Emitter
        SseEmitter emitter = sseManager.createEmitter(finalSessionId, 0L);

        // 异步处理流式请求
        handleStreamingRequest(request, finalSessionId, emitter);

        return emitter;
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSessionInfo(@PathVariable String sessionId) {
        McpSession session = sessions.get(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> info = new java.util.HashMap<>();
        info.put("sessionId", session.getSessionId());
        info.put("status", session.getStatus());
        info.put("protocolVersion", session.getProtocolVersion());
        info.put("createTime", session.getCreateTime());
        info.put("lastActivityTime", session.getLastActivityTime());
        info.put("subscribedResources", session.getSubscribedResources().size());

        return ResponseEntity.ok(info);
    }

    /**
     * 关闭会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> closeSession(@PathVariable String sessionId) {
        McpSession session = sessions.remove(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        session.setStatus(McpProtocolConstants.SESSION_STATUS_CLOSED);
        sseManager.removeEmitter(sessionId);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("sessionId", sessionId);
        result.put("message", "Session closed successfully");

        return ResponseEntity.ok(result);
    }

    // ===== 私有方法 =====

    private void handleStreamingRequest(McpRequestV1 request, String sessionId, SseEmitter emitter) {
        try {
            // 发送开始事件
            emitter.send(SseEmitter.event()
                    .name("start")
                    .data("{\"status\":\"started\",\"sessionId\":\"" + sessionId + "\"}"));

            // TODO: 实现实际的流式处理逻辑
            // 这里需要调用协议服务处理请求并流式返回结果

            // 模拟流式响应
            for (int i = 0; i < 5; i++) {
                Thread.sleep(100);
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data("{\"progress\":" + (i + 1) * 20 + ",\"total\":100}"));
            }

            // 发送完成事件
            emitter.send(SseEmitter.event()
                    .name("complete")
                    .data("{\"status\":\"completed\"}"));

            emitter.complete();

        } catch (Exception e) {
            logger.error("Error in streaming request", e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"" + e.getMessage() + "\"}"));
                emitter.completeWithError(e);
            } catch (IOException ex) {
                logger.error("Error sending error event", ex);
            }
        }
    }

    private String extractProtocolVersion(McpRequestV1 request) {
        if (request.getParams() != null) {
            Object version = request.getParams().get("protocolVersion");
            if (version != null) {
                return version.toString();
            }
        }
        return McpProtocolConstants.PROTOCOL_VERSION;
    }
}
