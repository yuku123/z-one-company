package com.zifang.z.agent.core.demo;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class LocalModelClient {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String modelName; // 本地模型名（如qwen:7b）

    // JDK1.8 构造器
    public LocalModelClient(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 调用本地模型，返回响应文本
     */
    public String chat(String prompt) {
        // JDK1.8 兼容：CloseableHttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false); // 非流式输出
            requestBody.put("temperature", 0.1); // 贴合原项目默认温度，保证输出稳定
            requestBody.put("max_tokens", 4096); // 最大 tokens，避免溢出

            // 构造POST请求
            HttpPost httpPost = new HttpPost(OLLAMA_URL);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(requestBody), "UTF-8"));

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(responseStr, Map.class);
            return responseMap.get("response").toString();

        } catch (Exception e) {
            throw new RuntimeException("调用本地模型失败", e);
        }
    }
}