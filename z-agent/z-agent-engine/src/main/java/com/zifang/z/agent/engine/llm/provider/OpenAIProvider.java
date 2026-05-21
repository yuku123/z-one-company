package com.zifang.z.agent.engine.llm.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.engine.llm.model.ChatCompletionsRequest;
import com.zifang.z.agent.engine.llm.model.ChatCompletionsResponse;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * OpenAI provider implementation.
 */
public class OpenAIProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAIProvider.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String baseUrl;
    private String apiKey;

    public OpenAIProvider() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = DEFAULT_BASE_URL;
    }

    public OpenAIProvider(String baseUrl, String apiKey) {
        this();
        if (StringUtils.isNotBlank(baseUrl)) {
            this.baseUrl = baseUrl;
        }
        this.apiKey = apiKey;
    }

    @Override
    public String getProviderCode() {
        return "openai";
    }

    @Override
    public String getDefaultBaseUrl() {
        return DEFAULT_BASE_URL;
    }

    @Override
    public ChatCompletionsResponse chat(ChatCompletionsRequest request) throws IOException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(request), JSON);
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json");

        if (StringUtils.isNotBlank(apiKey)) {
            builder.addHeader("Authorization", "Bearer " + apiKey);
        }

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI request failed: " + response);
            }
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, ChatCompletionsResponse.class);
        }
    }

    @Override
    public boolean isAvailable() {
        return StringUtils.isNotBlank(apiKey);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
