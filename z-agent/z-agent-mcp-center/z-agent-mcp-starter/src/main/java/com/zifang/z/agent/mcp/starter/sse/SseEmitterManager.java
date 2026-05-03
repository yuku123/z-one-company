package com.zifang.z.agent.mcp.starter.sse;

import com.zifang.z.agent.mcp.starter.protocol.McpResponseV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SSE Emitter 管理器
 */
@Component
public class SseEmitterManager {

    private static final Logger logger = LoggerFactory.getLogger(SseEmitterManager.class);

    /**
     * 默认超时时间（毫秒）
     */
    private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30 分钟

    /**
     * 心跳间隔（毫秒）
     */
    private static final long HEARTBEAT_INTERVAL = 30000L; // 30 秒

    /**
     * Emitter 存储：sessionId -> SseEmitter
     */
    private final Map<String, SseEmitterWrapper> emitters = new ConcurrentHashMap<>();

    /**
     * 心跳调度器
     */
    private ScheduledExecutorService heartbeatExecutor;

    @PostConstruct
    public void init() {
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "sse-heartbeat");
            thread.setDaemon(true);
            return thread;
        });
        startHeartbeat();
        logger.info("SSE Emitter Manager initialized");
    }

    @PreDestroy
    public void destroy() {
        shutdown();
        logger.info("SSE Emitter Manager destroyed");
    }

    /**
     * 创建新的 SSE Emitter
     */
    public SseEmitter createEmitter(String sessionId) {
        return createEmitter(sessionId, DEFAULT_TIMEOUT);
    }

    /**
     * 创建新的 SSE Emitter（指定超时）
     */
    public SseEmitter createEmitter(String sessionId, long timeout) {
        // 如果已存在，先移除旧的
        removeEmitter(sessionId);

        SseEmitter emitter = timeout > 0 ? new SseEmitter(timeout) : new SseEmitter();
        SseEmitterWrapper wrapper = new SseEmitterWrapper(sessionId, emitter);

        emitter.onCompletion(() -> {
            logger.debug("SSE emitter completed: {}", sessionId);
            emitters.remove(sessionId);
        });

        emitter.onTimeout(() -> {
            logger.warn("SSE emitter timeout: {}", sessionId);
            emitters.remove(sessionId);
        });

        emitter.onError(e -> {
            logger.error("SSE emitter error: {}", sessionId, e);
            emitters.remove(sessionId);
        });

        emitters.put(sessionId, wrapper);
        logger.info("SSE emitter created: {}", sessionId);

        return emitter;
    }

    /**
     * 移除 Emitter
     */
    public void removeEmitter(String sessionId) {
        SseEmitterWrapper wrapper = emitters.remove(sessionId);
        if (wrapper != null) {
            try {
                wrapper.getEmitter().complete();
            } catch (Exception e) {
                logger.warn("Error completing emitter: {}", sessionId, e);
            }
        }
    }

    /**
     * 发送事件到指定 session
     */
    public boolean sendEvent(String sessionId, String eventName, Object data) {
        SseEmitterWrapper wrapper = emitters.get(sessionId);
        if (wrapper == null) {
            logger.warn("Emitter not found: {}", sessionId);
            return false;
        }

        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name(eventName)
                    .data(data, MediaType.APPLICATION_JSON);

            wrapper.getEmitter().send(event);
            wrapper.updateLastActivity();
            return true;
        } catch (IOException e) {
            logger.error("Error sending event to {}: {}", sessionId, e.getMessage());
            emitters.remove(sessionId);
            return false;
        }
    }

    /**
     * 发送 MCP 响应
     */
    public boolean sendResponse(String sessionId, McpResponseV1 response) {
        return sendEvent(sessionId, "message", response);
    }

    /**
     * 广播事件到所有连接
     */
    public void broadcastEvent(String eventName, Object data) {
        emitters.forEach((sessionId, wrapper) -> {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .name(eventName)
                        .data(data, MediaType.APPLICATION_JSON);
                wrapper.getEmitter().send(event);
                wrapper.updateLastActivity();
            } catch (IOException e) {
                logger.error("Error broadcasting to {}: {}", sessionId, e.getMessage());
                emitters.remove(sessionId);
            }
        });
    }

    /**
     * 获取活动连接数
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }

    /**
     * 检查 session 是否活跃
     */
    public boolean isActive(String sessionId) {
        return emitters.containsKey(sessionId);
    }

    /**
     * 启动心跳
     */
    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                long now = System.currentTimeMillis();
                emitters.entrySet().removeIf(entry -> {
                    SseEmitterWrapper wrapper = entry.getValue();
                    // 如果超过 2 个心跳周期没有活动，认为已失效
                    if (now - wrapper.getLastActivityTime() > HEARTBEAT_INTERVAL * 2) {
                        logger.warn("Removing inactive emitter: {}", entry.getKey());
                        try {
                            wrapper.getEmitter().complete();
                        } catch (Exception e) {
                            // ignore
                        }
                        return true;
                    }
                    return false;
                });

                // 发送心跳
                broadcastEvent("ping", new HeartbeatEvent(System.currentTimeMillis()));
            } catch (Exception e) {
                logger.error("Heartbeat error", e);
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭管理器
     */
    public void shutdown() {
        heartbeatExecutor.shutdown();
        emitters.forEach((sessionId, wrapper) -> {
            try {
                wrapper.getEmitter().complete();
            } catch (Exception e) {
                // ignore
            }
        });
        emitters.clear();
    }

    /**
     * Emitter 包装类
     */
    private static class SseEmitterWrapper {
        private final String sessionId;
        private final SseEmitter emitter;
        private volatile long lastActivityTime;

        public SseEmitterWrapper(String sessionId, SseEmitter emitter) {
            this.sessionId = sessionId;
            this.emitter = emitter;
            this.lastActivityTime = System.currentTimeMillis();
        }

        public String getSessionId() {
            return sessionId;
        }

        public SseEmitter getEmitter() {
            return emitter;
        }

        public long getLastActivityTime() {
            return lastActivityTime;
        }

        public void updateLastActivity() {
            this.lastActivityTime = System.currentTimeMillis();
        }
    }

    /**
     * 心跳事件
     */
    private static class HeartbeatEvent {
        private final long timestamp;
        private final String type = "heartbeat";

        public HeartbeatEvent(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getType() {
            return type;
        }
    }
}