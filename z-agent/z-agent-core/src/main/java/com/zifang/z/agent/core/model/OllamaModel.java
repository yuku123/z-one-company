package com.zifang.z.agent.core.model;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.core.model.define.*;
import com.zifang.z.agent.core.tool.ToolSchema;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OllamaModel implements Model {

    private boolean inited = false;

    private LlmCallerConfig config;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void init(LlmCallerConfig llmCallerConfig){
        this.config = llmCallerConfig;
    }

    @Override
    public Flux<ModelResponse> stream(ModelRequest modelRequest) {
        return null;
    }

    @Override
    public Flux<ModelResponse> stream(List<ModelMessage> messages, List<ToolSchema> tools, ModelOptions options) {
        return null;
    }

    @Override
    public String identity() {
        return "OllamaModel";
    }

    @Override
    public String generate(List<ModelMessage> modelMessages) {

        String prompt = adapter(modelMessages);

        // JDK1.8 兼容：CloseableHttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModelName());
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false); // 非流式输出
            requestBody.put("temperature", 0.1); // 贴合原项目默认温度，保证输出稳定
            requestBody.put("max_tokens", 4096); // 最大 tokens，避免溢出

            // 构造POST请求
            HttpPost httpPost = new HttpPost(config.getBaseUrl() + "/api/generate");
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

    @Override
    public String chat(List<ModelMessage> modelMessages) {

        // JDK1.8 兼容：CloseableHttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModelName());
            requestBody.put("messages", modelMessages);
            requestBody.put("stream", false); // 非流式输出
            requestBody.put("temperature", 0.1); // 贴合原项目默认温度，保证输出稳定
            requestBody.put("max_tokens", 4096); // 最大 tokens，避免溢出

            // 构造POST请求
            HttpPost httpPost = new HttpPost(config.getBaseUrl() + "/api/chat");
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity(JSON.toJSONString(requestBody), "UTF-8"));

            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(responseStr, Map.class);
            return ((Map)responseMap.get("message")).get("content").toString();

        } catch (Exception e) {
            throw new RuntimeException("调用本地模型失败", e);
        }
    }

    private String adapter(List<ModelMessage> modelMessages) {
        StringBuilder sb = new StringBuilder();
        for (ModelMessage modelMessage : modelMessages) {
            sb.append(modelMessage.getRole() + ":" + modelMessage.getContent() + "\n");
        }
        return sb.toString();
    }
}
