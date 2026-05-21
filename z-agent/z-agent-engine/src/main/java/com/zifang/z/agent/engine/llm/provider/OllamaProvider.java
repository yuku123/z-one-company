package com.zifang.z.agent.engine.llm.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.engine.llm.model.ChatCompletionsRequest;
import com.zifang.z.agent.engine.llm.model.ChatCompletionsResponse;
import com.zifang.z.agent.engine.llm.model.ChatMessage;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Ollama provider implementation.
 */
public class OllamaProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(OllamaProvider.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String baseUrl;

    public OllamaProvider() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = DEFAULT_BASE_URL;
    }

    public OllamaProvider(String baseUrl) {
        this();
        if (StringUtils.isNotBlank(baseUrl)) {
            this.baseUrl = baseUrl;
        }
    }

    @Override
    public String getProviderCode() {
        return "ollama";
    }

    @Override
    public String getDefaultBaseUrl() {
        return DEFAULT_BASE_URL;
    }

    @Override
    public ChatCompletionsResponse chat(ChatCompletionsRequest request) throws IOException {
        // Transform OpenAI format to Ollama format
        OllamaChatRequest ollamaRequest = toOllamaRequest(request);

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(ollamaRequest), JSON);
        Request httpRequest = new Request.Builder()
                .url(baseUrl + "/api/chat")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ollama request failed: " + response);
            }
            String responseBody = response.body().string();
            return fromOllamaResponse(responseBody, request.getModel());
        }
    }

    private OllamaChatRequest toOllamaRequest(ChatCompletionsRequest request) {
        OllamaChatRequest ollamaRequest = new OllamaChatRequest();
        ollamaRequest.setModel(request.getModel());
        ollamaRequest.setStream(false);

        List<OllamaMessage> ollamaMessages = new java.util.ArrayList<>();
        for (ChatMessage msg : request.getMessages()) {
            OllamaMessage ollamaMsg = new OllamaMessage();
            ollamaMsg.setRole(msg.getRole());
            ollamaMsg.setContent(msg.getContent());
            ollamaMessages.add(ollamaMsg);
        }
        ollamaRequest.setMessages(ollamaMessages);
        return ollamaRequest;
    }

    private ChatCompletionsResponse fromOllamaResponse(String responseBody, String model) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        ChatCompletionsResponse resp = new ChatCompletionsResponse();
        resp.setId("chatcmpl-" + System.currentTimeMillis());
        resp.setObject("chat.completion");
        resp.setCreated(System.currentTimeMillis() / 1000);
        resp.setModel(model);

        ChatCompletionsResponse.ChatChoice choice = new ChatCompletionsResponse.ChatChoice();
        choice.setIndex(0);
        choice.setFinishReason(root.path("done").asBoolean() ? "stop" : "tool_calls");

        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setRole("assistant");

        String content = root.path("message").path("content").asText();
        assistantMsg.setContent(content);

        choice.setMessage(assistantMsg);
        resp.setChoices(java.util.Collections.singletonList(choice));

        // Usage
        ChatCompletionsResponse.Usage usage = new ChatCompletionsResponse.Usage();
        usage.setPromptTokens(root.path("prompt_eval_count").asInt(0));
        usage.setCompletionTokens(root.path("eval_count").asInt(0));
        usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
        resp.setUsage(usage);

        return resp;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Ollama internal models
    private static class OllamaChatRequest {
        private String model;
        private List<OllamaMessage> messages;
        private boolean stream;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public List<OllamaMessage> getMessages() { return messages; }
        public void setMessages(List<OllamaMessage> messages) { this.messages = messages; }
        public boolean isStream() { return stream; }
        public void setStream(boolean stream) { this.stream = stream; }
    }

    private static class OllamaMessage {
        private String role;
        private String content;

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
