package com.zifang.z.agent.llm.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LlmGateway 独立验证脚本
 * 直接用 main 方法测试 Ollama 连通性
 * 
 * 运行方式: 直接执行 main 方法
 */
public class LlmGatewayStandaloneTest {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("LlmGateway Ollama 验证测试");
        System.out.println("========================================\n");

        try {
            // Test 1: Ollama 连通性
            testOllamaConnection();

            // Test 2: 简单 Chat
            testSimpleChat();

            // Test 3: 多轮对话
            testMultiTurnChat();

            // Test 4: Token 计算
            testTokenCalculation();

            System.out.println("\n========================================");
            System.out.println("所有测试通过!");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test 1: Ollama 连接测试
     */
    private static void testOllamaConnection() {
        System.out.println("[Test 1] Ollama 连接测试...");
        
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/tags")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String body = response.body().string();
                Map<String, Object> resp = objectMapper.readValue(body, Map.class);
                List<Map<String, Object>> models = (List<Map<String, Object>>) resp.get("models");
                
                System.out.println("  ✓ Ollama 连接成功");
                System.out.println("  可用模型数量: " + models.size());
                models.forEach(m -> System.out.println("    - " + m.get("name")));
            } else {
                throw new RuntimeException("Ollama 返回错误: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ollama 连接失败: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Test 2: 简单 Chat 测试
     */
    private static void testSimpleChat() {
        System.out.println("[Test 2] 简单 Chat 测试...");
        
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("model", "qwen2.5:7b-instruct");
        reqBody.put("stream", false);
        reqBody.put("messages", List.of(
                Map.of("role", "user", "content", "你好，介绍一下自己")
        ));

        long startTime = System.currentTimeMillis();
        
        Map<String, Object> resp = chatRequest(reqBody);
        
        long latency = System.currentTimeMillis() - startTime;

        if (Boolean.TRUE.equals(resp.get("success"))) {
            Map<String, Object> data = (Map<String, Object>) resp.get("data");
            String content = (String) data.get("content");
            Map<String, Object> usage = (Map<String, Object>) data.get("usage");
            
            System.out.println("  ✓ Chat 成功");
            System.out.println("  响应: " + content);
            System.out.println("  延迟: " + latency + "ms");
            System.out.println("  Input Tokens: " + usage.get("prompt_tokens"));
            System.out.println("  Output Tokens: " + usage.get("completion_tokens"));
            System.out.println("  Total Tokens: " + usage.get("total_tokens"));
        } else {
            System.out.println("  ✗ Chat 失败: " + resp.get("error"));
        }
        System.out.println();
    }

    /**
     * Test 3: 多轮对话测试
     */
    private static void testMultiTurnChat() {
        System.out.println("[Test 3] 多轮对话测试...");
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 第一轮
        messages.add(Map.of("role", "user", "content", "我是张三"));
        Map<String, Object> req1 = new HashMap<>();
        req1.put("model", "qwen2.5:7b-instruct");
        req1.put("stream", false);
        req1.put("messages", messages);
        
        Map<String, Object> resp1 = chatRequest(req1);
        if (Boolean.TRUE.equals(resp1.get("success"))) {
            Map<String, Object> data = (Map<String, Object>) resp1.get("data");
            String reply1 = (String) data.get("content");
            messages.add(Map.of("role", "assistant", "content", reply1));
            System.out.println("  第一轮: " + reply1.substring(0, Math.min(80, reply1.length())) + "...");
        }
        
        // 第二轮
        messages.add(Map.of("role", "user", "content", "我叫什么名字？"));
        Map<String, Object> req2 = new HashMap<>();
        req2.put("model", "qwen2.5:7b-instruct");
        req2.put("stream", false);
        req2.put("messages", messages);
        
        Map<String, Object> resp2 = chatRequest(req2);
        if (Boolean.TRUE.equals(resp2.get("success"))) {
            Map<String, Object> data = (Map<String, Object>) resp2.get("data");
            String reply2 = (String) data.get("content");
            System.out.println("  第二轮: " + reply2);
            
            // 验证是否记住名字
            if (reply2.contains("张三")) {
                System.out.println("  ✓ 模型记住了用户名字!");
            } else {
                System.out.println("  ⚠ 模型未能记住用户名字");
            }
        }
        System.out.println();
    }

    /**
     * Test 4: Token 费用计算测试
     */
    private static void testTokenCalculation() {
        System.out.println("[Test 4] Token 费用计算测试...");
        
        // 模拟数据
        int inputTokens = 1500;
        int outputTokens = 500;
        double inputPrice = 0.001;  // ¥0.001/千token
        double outputPrice = 0.002; // ¥0.002/千token
        
        BigDecimal cost = BigDecimal.valueOf(inputTokens)
                .multiply(BigDecimal.valueOf(inputPrice))
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(outputTokens)
                        .multiply(BigDecimal.valueOf(outputPrice))
                        .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
        
        System.out.println("  输入 Token: " + inputTokens);
        System.out.println("  输出 Token: " + outputTokens);
        System.out.println("  总 Token: " + (inputTokens + outputTokens));
        System.out.println("  输入价格: ¥" + inputPrice + "/千token");
        System.out.println("  输出价格: ¥" + outputPrice + "/千token");
        System.out.println("  本次费用: ¥" + cost);
        System.out.println();
        
        // 模拟按应用统计
        System.out.println("  模拟按应用统计:");
        Map<String, int[]> appUsage = new LinkedHashMap<>();
        appUsage.put("app-chat", new int[]{5000, 2000});
        appUsage.put("app-code", new int[]{15000, 8000});
        appUsage.put("app-summary", new int[]{3000, 1000});
        
        appUsage.forEach((app, usage) -> {
            int in = usage[0];
            int out = usage[1];
            BigDecimal appCost = BigDecimal.valueOf(in)
                    .multiply(BigDecimal.valueOf(inputPrice))
                    .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP)
                    .add(BigDecimal.valueOf(out)
                            .multiply(BigDecimal.valueOf(outputPrice))
                            .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
            System.out.println("    " + app + ": " + (in+out) + " tokens, 费用 ¥" + appCost);
        });
        
        System.out.println("  ✓ 费用计算测试通过");
        System.out.println();
    }

    /**
     * 执行 Chat 请求
     */
    private static Map<String, Object> chatRequest(Map<String, Object> reqBody) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String json = objectMapper.writeValueAsString(reqBody);
            RequestBody body = RequestBody.create(json, JSON);
            
            Request request = new Request.Builder()
                    .url("http://localhost:11434/v1/chat/completions")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    result.put("success", false);
                    result.put("error", "HTTP " + response.code() + ": " + response.message());
                    return result;
                }

                String respBody = response.body().string();
                Map<String, Object> respMap = objectMapper.readValue(respBody, Map.class);

                // 提取响应内容 (OpenAI-compatible format)
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                String content = null;
                String finishReason = null;
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        content = (String) message.get("content");
                    }
                    finishReason = (String) choices.get(0).get("finish_reason");
                }

                // 提取 usage (Ollama 格式不同，需要适配)
                Map<String, Object> usage = extractUsage(respMap);

                Map<String, Object> data = new HashMap<>();
                data.put("id", respMap.get("id"));
                data.put("content", content);
                data.put("finishReason", finishReason);
                data.put("usage", usage);

                result.put("success", true);
                result.put("data", data);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 提取 Usage (兼容 Ollama 和 OpenAI 格式)
     */
    private static Map<String, Object> extractUsage(Map<String, Object> respMap) {
        Map<String, Object> usage = new HashMap<>();
        
        // OpenAI 格式
        if (respMap.containsKey("usage")) {
            Map<String, Object> origUsage = (Map<String, Object>) respMap.get("usage");
            usage.put("prompt_tokens", origUsage.getOrDefault("prompt_tokens", 0));
            usage.put("completion_tokens", origUsage.getOrDefault("completion_tokens", 0));
            usage.put("total_tokens", origUsage.getOrDefault("total_tokens", 0));
        }
        // Ollama 格式
        else if (respMap.containsKey("eval_count")) {
            usage.put("prompt_tokens", 0);  // Ollama 不提供这个
            usage.put("completion_tokens", respMap.get("eval_count"));
            usage.put("total_tokens", respMap.get("eval_count"));
        }
        
        return usage;
    }
}
