package com.zifang.z.agent.llm.gateway.dto;

/**
 * LLM Chat请求上下文，用于记录调用信息
 */
public class LlmChatContext {

    private String traceId;
    private String appCode;
    private String instanceCode;
    private String userId;
    private String userName;
    private String conversationCode;
    private String providerCode;
    private String modelCode;
    private Double inputPrice;  // 元/千token
    private Double outputPrice; // 元/千token

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final LlmChatContext ctx = new LlmChatContext();

        public Builder traceId(String traceId) { ctx.traceId = traceId; return this; }
        public Builder appCode(String appCode) { ctx.appCode = appCode; return this; }
        public Builder instanceCode(String instanceCode) { ctx.instanceCode = instanceCode; return this; }
        public Builder userId(String userId) { ctx.userId = userId; return this; }
        public Builder userName(String userName) { ctx.userName = userName; return this; }
        public Builder conversationCode(String conversationCode) { ctx.conversationCode = conversationCode; return this; }
        public Builder providerCode(String providerCode) { ctx.providerCode = providerCode; return this; }
        public Builder modelCode(String modelCode) { ctx.modelCode = modelCode; return this; }
        public Builder inputPrice(Double inputPrice) { ctx.inputPrice = inputPrice; return this; }
        public Builder outputPrice(Double outputPrice) { ctx.outputPrice = outputPrice; return this; }
        public LlmChatContext build() { return ctx; }
    }

    public String getTraceId() { return traceId; }
    public String getAppCode() { return appCode; }
    public String getInstanceCode() { return instanceCode; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getConversationCode() { return conversationCode; }
    public String getProviderCode() { return providerCode; }
    public String getModelCode() { return modelCode; }
    public Double getInputPrice() { return inputPrice; }
    public Double getOutputPrice() { return outputPrice; }
}
