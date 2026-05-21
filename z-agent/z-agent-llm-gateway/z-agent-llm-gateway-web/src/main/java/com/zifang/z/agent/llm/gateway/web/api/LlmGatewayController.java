package com.zifang.z.agent.llm.gateway.web.api;

import com.zifang.z.agent.llm.gateway.dto.LlmChatContext;
import com.zifang.z.agent.llm.gateway.dto.UsageOverviewResp;
import com.zifang.z.agent.llm.gateway.dto.UsageStatisticsResp;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService;
import org.springframework.web.bind.annotation.*;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService.ChatRequest;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService.ChatResponse;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * LLM Gateway Web API
 * 统一入口：所有LLM调用通过此Controller，记录完整用量
 */
@RestController
@RequestMapping("/api/llm-gateway")
public class LlmGatewayController {

    @Resource
    private LlmGatewayService llmGatewayService;

    /**
     * 统一的Chat接口
     */
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatWithContext request) {
        LlmChatContext ctx = LlmChatContext.builder()
                .traceId(request.getTraceId())
                .appCode(request.getAppCode())
                .instanceCode(request.getInstanceCode())
                .userId(request.getUserId())
                .userName(request.getUserName())
                .conversationCode(request.getConversationCode())
                .providerCode(request.getProviderCode())
                .modelCode(request.getModelCode())
                .inputPrice(request.getInputPrice())
                .outputPrice(request.getOutputPrice())
                .build();

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessages(request.getMessages());
        chatRequest.setTools(request.getTools());

        return llmGatewayService.chat(chatRequest, ctx);
    }

    /**
     * 用量概览
     */
    @GetMapping("/usage/overview")
    public UsageOverviewResp getOverview(
            @RequestParam(required = false) String appCode,
            @RequestParam(required = false) String userId) {
        return llmGatewayService.getOverview(appCode, userId, null, null);
    }

    /**
     * 按应用统计
     */
    @GetMapping("/usage/by-app")
    public List<UsageStatisticsResp> getUsageByApp(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return llmGatewayService.getUsageByApp(startDate, endDate);
    }

    /**
     * 按用户统计
     */
    @GetMapping("/usage/by-user")
    public List<UsageStatisticsResp> getUsageByUser(
            @RequestParam(required = false) String appCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return llmGatewayService.getUsageByUser(appCode, startDate, endDate);
    }

    /**
     * 用量趋势
     */
    @GetMapping("/usage/trend")
    public List<UsageStatisticsResp> getUsageTrend(
            @RequestParam(required = false) String appCode,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "7") int days) {
        return llmGatewayService.getUsageTrend(appCode, userId, days);
    }

    /**
     * 配置Provider
     */
    @PostMapping("/config/provider")
    public void configureProvider(@RequestBody Map<String, String> config) {
        llmGatewayService.configureProvider(
                config.get("providerCode"),
                config.get("baseUrl"),
                config.get("apiKey"));
    }

    /**
     * 请求体：Chat + Context
     */
    public static class ChatWithContext {
        private String traceId;
        private String appCode;
        private String instanceCode;
        private String userId;
        private String userName;
        private String conversationCode;
        private String providerCode;
        private String modelCode;
        private Double inputPrice;
        private Double outputPrice;
        private List<Map<String, String>> messages;
        private List<Map<String, Object>> tools;

        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public String getAppCode() { return appCode; }
        public void setAppCode(String appCode) { this.appCode = appCode; }
        public String getInstanceCode() { return instanceCode; }
        public void setInstanceCode(String instanceCode) { this.instanceCode = instanceCode; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getConversationCode() { return conversationCode; }
        public void setConversationCode(String conversationCode) { this.conversationCode = conversationCode; }
        public String getProviderCode() { return providerCode; }
        public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
        public String getModelCode() { return modelCode; }
        public void setModelCode(String modelCode) { this.modelCode = modelCode; }
        public Double getInputPrice() { return inputPrice; }
        public void setInputPrice(Double inputPrice) { this.inputPrice = inputPrice; }
        public Double getOutputPrice() { return outputPrice; }
        public void setOutputPrice(Double outputPrice) { this.outputPrice = outputPrice; }
        public List<Map<String, String>> getMessages() { return messages; }
        public void setMessages(List<Map<String, String>> messages) { this.messages = messages; }
        public List<Map<String, Object>> getTools() { return tools; }
        public void setTools(List<Map<String, Object>> tools) { this.tools = tools; }
    }
}
