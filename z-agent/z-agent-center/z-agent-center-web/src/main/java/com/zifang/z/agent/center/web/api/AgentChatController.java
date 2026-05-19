package com.zifang.z.agent.center.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.agent.conversation.entity.AgentConversation;
import com.zifang.z.agent.center.core.agent.conversation.service.AgentConversationService;
import com.zifang.z.agent.center.core.agent.runtime.service.AgentRuntimeService;
import com.zifang.z.agent.center.web.api.request.AgentChatReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Agent对话")
@RestController
@RequestMapping("/api/agent/chat")
public class AgentChatController {

    @Resource
    private AgentRuntimeService runtimeService;

    @Resource
    private AgentConversationService conversationService;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "发送对话消息")
    @PostMapping("/send")
    public Result<ChatResp> send(@RequestBody AgentChatReq req) {
        AgentConversation conversation = runtimeService.chat(
                req.getInstanceCode(),
                req.getMessage(),
                req.getUserId(),
                req.getUserName()
        );
        return Result.success(toChatResp(conversation));
    }

    @Operation(summary = "查询历史消息")
    @GetMapping("/history")
    public Result<List<ChatResp>> history(@RequestParam String instanceCode, @RequestParam(defaultValue = "50") int limit) {
        List<AgentConversation> list = conversationService.listByInstance(instanceCode, limit);
        return Result.success(list.stream().map(this::toChatResp).collect(Collectors.toList()));
    }

    @Operation(summary = "清除会话历史")
    @PostMapping("/clear")
    public Result<Void> clear(@RequestParam String instanceCode) {
        runtimeService.clearHistory(instanceCode);
        return Result.success();
    }

    private ChatResp toChatResp(AgentConversation c) {
        ChatResp resp = new ChatResp();
        BeanUtils.copyProperties(c, resp);
        resp.setGmtCreate(c.getGmtCreate() != null ? c.getGmtCreate().format(DF) : null);
        return resp;
    }

    public static class ChatResp {
        private String conversationCode;
        private String instanceCode;
        private String userId;
        private String userName;
        private String userMessage;
        private String assistantMessage;
        private String modelName;
        private Integer tokenCount;
        private Integer latencyMs;
        private String status;
        private String errorMsg;
        private String gmtCreate;

        public String getConversationCode() { return conversationCode; }
        public void setConversationCode(String conversationCode) { this.conversationCode = conversationCode; }
        public String getInstanceCode() { return instanceCode; }
        public void setInstanceCode(String instanceCode) { this.instanceCode = instanceCode; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getUserMessage() { return userMessage; }
        public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
        public String getAssistantMessage() { return assistantMessage; }
        public void setAssistantMessage(String assistantMessage) { this.assistantMessage = assistantMessage; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public Integer getTokenCount() { return tokenCount; }
        public void setTokenCount(Integer tokenCount) { this.tokenCount = tokenCount; }
        public Integer getLatencyMs() { return latencyMs; }
        public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorMsg() { return errorMsg; }
        public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
        public String getGmtCreate() { return gmtCreate; }
        public void setGmtCreate(String gmtCreate) { this.gmtCreate = gmtCreate; }
    }
}
