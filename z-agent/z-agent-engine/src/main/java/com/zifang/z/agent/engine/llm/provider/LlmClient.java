package com.zifang.z.agent.engine.llm.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.engine.llm.model.*;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM Client that manages multiple providers.
 */
public class LlmClient {

    private static final Logger log = LoggerFactory.getLogger(LlmClient.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, LlmProviderConfig> providerConfigs = new ConcurrentHashMap<>();
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LlmClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();

        // Register default providers
        registerProvider(new OllamaProvider());
        registerProvider(new OpenAIProvider());
    }

    /**
     * Register a provider.
     */
    public void registerProvider(LlmProvider provider) {
        providers.put(provider.getProviderCode(), provider);
        log.info("Registered LLM provider: {}", provider.getProviderCode());
    }

    /**
     * Configure a provider.
     */
    public void configureProvider(String providerCode, String baseUrl, String apiKey) {
        LlmProviderConfig config = new LlmProviderConfig();
        config.setProviderCode(providerCode);
        config.setBaseUrl(baseUrl);
        config.setApiKey(apiKey);
        providerConfigs.put(providerCode, config);

        LlmProvider provider = providers.get(providerCode);
        if (provider instanceof OllamaProvider) {
            ((OllamaProvider) provider).setBaseUrl(baseUrl);
        } else if (provider instanceof OpenAIProvider) {
            ((OpenAIProvider) provider).setBaseUrl(baseUrl);
            ((OpenAIProvider) provider).setApiKey(apiKey);
        }
    }

    /**
     * Chat with the model.
     */
    public ChatResult chat(String providerCode, String model, List<ChatMessage> messages) throws IOException {
        return chat(providerCode, model, messages, null);
    }

    /**
     * Chat with tools enabled.
     */
    public ChatResult chat(String providerCode, String model, List<ChatMessage> messages,
                          List<OpenAITool> tools) throws IOException {
        LlmProvider provider = providers.get(providerCode);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerCode);
        }

        ChatCompletionsRequest request = ChatCompletionsRequest.builder()
                .model(model)
                .messages(messages)
                .tools(tools)
                .stream(false)
                .build();

        ChatCompletionsResponse response = provider.chat(request);
        return toChatResult(response);
    }

    private ChatResult toChatResult(ChatCompletionsResponse response) {
        ChatResult result = new ChatResult();
        result.setId(response.getId());
        result.setContent(response.getFirstContent());
        result.setToolCalls(response.getToolCalls());
        result.setFinishReason(response.getFinishReason());
        if (response.getUsage() != null) {
            result.setPromptTokens(response.getUsage().getPromptTokens());
            result.setCompletionTokens(response.getUsage().getCompletionTokens());
            result.setTotalTokens(response.getUsage().getTotalTokens());
        }
        return result;
    }

    public Set<String> getAvailableProviders() {
        return providers.keySet();
    }

    private static class LlmProviderConfig {
        private String providerCode;
        private String baseUrl;
        private String apiKey;

        public String getProviderCode() { return providerCode; }
        public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }

    /**
     * Chat result with tool call support.
     */
    public static class ChatResult {
        private String id;
        private String content;
        private List<ToolCall> toolCalls;
        private String finishReason;
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<ToolCall> getToolCalls() { return toolCalls; }
        public void setToolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }

        public boolean hasToolCalls() {
            return toolCalls != null && !toolCalls.isEmpty();
        }
    }
}
