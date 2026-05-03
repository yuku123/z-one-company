package com.zifang.z.agent.core.demo;


import com.zifang.z.agent.core.model.define.ModelMessage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话实体（对应原项目 Session），存储单个用户的对话历史和会话状态
 */
@Data
public class NanoBotSession {

    // 会话唯一标识（如 UUID）
    private String sessionId;

    // 对话历史（用户/助手/工具消息）
    private List<ModelMessage> modelMessageHistory = new ArrayList<>();

    // 最后一次记忆合并时间（贴合原项目记忆管理）
    private LocalDateTime lastConsolidateTime;

    // 会话状态（空闲/运行中/暂停）
    private SessionStatus status = SessionStatus.IDLE;

    // 会话状态枚举
    public enum SessionStatus {
        IDLE, RUNNING, PAUSED
    }

    // 新增消息到对话历史
    public void addMessage(ModelMessage modelMessage) {
        this.modelMessageHistory.add(modelMessage);
    }
}