package com.zifang.z.agent.mcp.service1.protocol;

import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * MCP 会话对象
 */
@Data
public class McpSession {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 客户端能力集
     */
    private ClientCapabilities clientCapabilities;

    /**
     * 协商后的协议版本
     */
    private String protocolVersion;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 最后活动时间
     */
    private long lastActivityTime;

    /**
     * 订阅的资源列表
     */
    private Set<String> subscribedResources;

    /**
     * 进度 token 映射
     */
    private Map<String, ProgressTracker> progressTrackers;

    /**
     * 会话级别的元数据
     */
    private Map<String, Object> metadata;

    @Data
    public static class ClientCapabilities {
        /**
         * 实验性功能
         */
        private Map<String, Object> experimental;

        /**
         * 采样能力
         */
        private SamplingCapability sampling;

        /**
         * 根路径能力
         */
        private RootsCapability roots;
    }

    @Data
    public static class SamplingCapability {
        private boolean enabled;
    }

    @Data
    public static class RootsCapability {
        private boolean listChanged;
    }

    @Data
    public static class ProgressTracker {
        private String token;
        private double progress;
        private double total;
        private String message;
        private long lastUpdateTime;
    }

    public McpSession() {
        this.createTime = System.currentTimeMillis();
        this.lastActivityTime = this.createTime;
        this.status = McpProtocolConstants.SESSION_STATUS_INITIALIZING;
        this.subscribedResources = new CopyOnWriteArraySet<>();
        this.progressTrackers = new ConcurrentHashMap<>();
        this.metadata = new ConcurrentHashMap<>();
    }

    public void updateActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

    public boolean isActive() {
        return McpProtocolConstants.SESSION_STATUS_ACTIVE.equals(this.status);
    }

    public void subscribeResource(String uri) {
        this.subscribedResources.add(uri);
    }

    public void unsubscribeResource(String uri) {
        this.subscribedResources.remove(uri);
    }

    public boolean isSubscribedTo(String uri) {
        return this.subscribedResources.contains(uri);
    }

    public ProgressTracker createProgressTracker(String token, double total) {
        ProgressTracker tracker = new ProgressTracker();
        tracker.setToken(token);
        tracker.setTotal(total);
        tracker.setProgress(0);
        tracker.setLastUpdateTime(System.currentTimeMillis());
        this.progressTrackers.put(token, tracker);
        return tracker;
    }

    public void updateProgress(String token, double progress, String message) {
        ProgressTracker tracker = this.progressTrackers.get(token);
        if (tracker != null) {
            tracker.setProgress(progress);
            tracker.setMessage(message);
            tracker.setLastUpdateTime(System.currentTimeMillis());
        }
    }

    public void removeProgressTracker(String token) {
        this.progressTrackers.remove(token);
    }
}
