package com.zifang.z.agent.engine.agent.runtime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.engine.agent.conversation.dto.ChatDto;
import com.zifang.z.agent.engine.agent.conversation.dto.AgentChatReq;
import com.zifang.z.agent.engine.agent.instance.entity.AgentInstance;
import com.zifang.z.agent.engine.agent.instance.service.AgentInstanceService;
import com.zifang.z.agent.engine.agent.conversation.service.AgentConversationService;
import com.zifang.z.agent.engine.app.entity.AgentApp;
import com.zifang.z.agent.engine.app.service.AgentAppService;
import com.zifang.z.agent.engine.agent.conversation.entity.AgentConversation;
import org.springframework.beans.BeanUtils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentRuntimeService {

    private static final Logger log = LoggerFactory.getLogger(AgentRuntimeService.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS).readTimeout(120, java.util.concurrent.TimeUnit.SECONDS).build();

    @Resource
    private AgentAppService agentAppService;

    @Resource
    private AgentInstanceService instanceService;

    @Resource
    private AgentConversationService conversationService;

    // 内存缓存会话上下文（生产环境应使用Redis）
    private final Map<String, List<ChatMessage>> conversationHistoryMap = new ConcurrentHashMap<>();
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 对话入口 - 返回DTO
     */
    public ChatDto chatResp(String instanceCode, String userMessage, String userId, String userName) {
        return toDto(chat(instanceCode, userMessage, userId, userName));
    }

    /**
     * 对话入口 - 返回Entity
     */
    public AgentConversation chat(String instanceCode, String userMessage, String userId, String userName) {
        long startTime = System.currentTimeMillis();

        // 1. 获取实例和应用配置
        AgentInstance instance = instanceService.getByInstanceCode(instanceCode);
        if (instance == null || !"ACTIVE".equals(instance.getStatus())) {
            return buildErrorConversation(instanceCode, userId, userName, userMessage, "实例不存在或已停用", startTime);
        }

        AgentApp app = agentAppService.getByAppCode(instance.getAppCode());
        if (app == null) {
            return buildErrorConversation(instanceCode, userId, userName, userMessage, "应用不存在", startTime);
        }

        // 2. 记录访问
        instanceService.recordVisit(instanceCode);

        // 3. 获取历史消息
        String conversationCode = instanceCode + "_" + System.currentTimeMillis() / 60000;
        List<ChatMessage> history = conversationHistoryMap.computeIfAbsent(conversationCode, k -> new ArrayList<>());

        // 添加系统prompt
        if (history.isEmpty() && StringUtils.isNotEmpty(app.getPrompt())) {
            history.add(new ChatMessage("system", app.getPrompt()));
        }

        // 添加用户消息
        history.add(new ChatMessage("user", userMessage));

        // 4. 调用LLM
        String assistantReply;
        try {
            assistantReply = callLlm(app.getModelProvider(), app.getModelName(), history);
        } catch (Exception e) {
            log.error("LLM调用失败", e);
            assistantReply = "抱歉，AI服务暂时不可用：" + e.getMessage();
        }

        // 5. 记录助手回复
        history.add(new ChatMessage("assistant", assistantReply));

        // 保持历史不超过50条
        while (history.size() > 50) {
            history.remove(1);
        }

        // 6. 保存会话记录
        AgentConversation conversation = new AgentConversation();
        conversation.setConversationCode(conversationCode);
        conversation.setInstanceCode(instanceCode);
        conversation.setUserId(userId);
        conversation.setUserName(userName);
        conversation.setUserMessage(userMessage);
        conversation.setAssistantMessage(assistantReply);
        conversation.setModelName(app.getModelName());
        conversation.setLatencyMs((int) (System.currentTimeMillis() - startTime));
        conversation.setStatus("SUCCESS");
        conversationService.saveEntity(conversation);

        return conversation;
    }

    /**
     * 调用LLM
     */
    private String callLlm(String provider, String modelName, List<ChatMessage> messages) throws IOException {
        String actualModel = StringUtils.defaultIfEmpty(modelName, "qwen2.5:7b");
        String baseUrl = OLLAMA_BASE_URL;

        // 构建请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", actualModel);

        List<Map<String, String>> ollamaMessages = new ArrayList<>();
        for (ChatMessage msg : messages) {
            Map<String, String> m = new HashMap<>();
            m.put("role", msg.role);
            m.put("content", msg.content);
            ollamaMessages.add(m);
        }
        requestBody.put("messages", ollamaMessages);
        requestBody.put("stream", false);

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(requestBody), JSON);
        Request request = new Request.Builder()
                .url(baseUrl + "/api/chat")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("LLM请求失败: " + response);
            }
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.path("message").path("content").asText();
        }
    }

    /**
     * 清除会话历史
     */
    public void clearHistory(String instanceCode) {
        conversationHistoryMap.entrySet().removeIf(entry -> entry.getKey().startsWith(instanceCode));
    }

    private AgentConversation buildErrorConversation(String instanceCode, String userId, String userName,
                                                     String userMessage, String errorMsg, long startTime) {
        AgentConversation conversation = new AgentConversation();
        conversation.setConversationCode("error_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        conversation.setInstanceCode(instanceCode);
        conversation.setUserId(userId);
        conversation.setUserName(userName);
        conversation.setUserMessage(userMessage);
        conversation.setAssistantMessage(errorMsg);
        conversation.setLatencyMs((int) (System.currentTimeMillis() - startTime));
        conversation.setStatus("FAILED");
        conversation.setErrorMsg(errorMsg);
        conversationService.saveEntity(conversation);
        return conversation;
    }

    /**
     * 简单消息对象
     */
    private static class ChatMessage {
        String role;
        String content;
        ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private ChatDto toDto(AgentConversation c) {
        if (c == null) return null;
        ChatDto dto = new ChatDto();
        BeanUtils.copyProperties(c, dto);
        dto.setGmtCreate(c.getGmtCreate() != null ? c.getGmtCreate().format(DF) : null);
        return dto;
    }
}
