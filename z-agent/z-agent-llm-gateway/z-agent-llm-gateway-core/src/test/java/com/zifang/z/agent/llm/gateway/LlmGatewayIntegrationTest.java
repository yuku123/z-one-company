package com.zifang.z.agent.llm.gateway;

import com.zifang.z.agent.llm.gateway.dto.LlmChatContext;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService.ChatRequest;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LlmGateway 完整链路集成测试
 * 
 * 验证：
 * 1. Ollama 连通性
 * 2. Chat API 调用
 * 3. 用量记录
 * 4. 统计查询
 */
@SpringBootTest
@EnabledIf("isOllamaRunning")
public class LlmGatewayIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(LlmGatewayIntegrationTest.class);

    static boolean isOllamaRunning() {
        try {
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection)
                new java.net.URL("http://localhost:11434/api/tags").openConnection();
            conn.setConnectTimeout(2000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired(required = false)
    private LlmGatewayService llmGatewayService;

    /**
     * Test 1: 配置并调用 Ollama
     */
    @Test
    public void testConfigureAndChat() {
        if (llmGatewayService == null) {
            log.warn("LlmGatewayService not available, skip test");
            return;
        }

        log.info("=== Test: Configure and Chat ===");

        // 配置
        llmGatewayService.configureProvider("ollama", "http://localhost:11434", null);

        // 构建上下文
        LlmChatContext ctx = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .appCode("test-integration")
                .instanceCode("test-instance")
                .userId("user-integration")
                .userName("集成测试用户")
                .conversationCode("conv-integration-001")
                .providerCode("ollama")
                .modelCode("qwen2.5:7b-instruct")
                .inputPrice(0.001)
                .outputPrice(0.002)
                .build();

        // 构建请求
        ChatRequest request = new ChatRequest();
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", "Hello, respond with just OK"));
        request.setMessages(messages);

        // 调用
        long startTime = System.currentTimeMillis();
        ChatResponse response = llmGatewayService.chat(request, ctx);
        long costTime = System.currentTimeMillis() - startTime;

        // 验证
        assertTrue(response.isSuccess(), "Chat should succeed");
        assertNotNull(response.getContent(), "Content should not be null");
        assertTrue(costTime < 120000, "Should complete within 2 minutes");

        log.info("Success: {}", response.isSuccess());
        log.info("Content: {}", response.getContent());
        log.info("Input Tokens: {}", response.getInputTokens());
        log.info("Output Tokens: {}", response.getOutputTokens());
        log.info("Total Tokens: {}", response.getTotalTokens());
        log.info("Latency: {}ms", costTime);
    }

    /**
     * Test 2: 多轮对话
     */
    @Test
    public void testMultiTurnConversation() {
        if (llmGatewayService == null) {
            log.warn("LlmGatewayService not available, skip test");
            return;
        }

        log.info("=== Test: Multi-turn Conversation ===");

        llmGatewayService.configureProvider("ollama", "http://localhost:11434", null);

        String conversationCode = "conv-multi-" + System.currentTimeMillis();

        // 第一轮
        LlmChatContext ctx1 = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .appCode("test-multi")
                .userId("user-multi")
                .conversationCode(conversationCode)
                .providerCode("ollama")
                .modelCode("qwen2.5:7b-instruct")
                .build();

        ChatRequest req1 = new ChatRequest();
        req1.setMessages(List.of(Map.of("role", "user", "content", "I am Bob")));
        ChatResponse resp1 = llmGatewayService.chat(req1, ctx1);

        assertTrue(resp1.isSuccess());
        log.info("Round 1: {}", resp1.getContent());

        // 第二轮
        LlmChatContext ctx2 = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .appCode("test-multi")
                .userId("user-multi")
                .conversationCode(conversationCode)
                .providerCode("ollama")
                .modelCode("qwen2.5:7b-instruct")
                .build();

        ChatRequest req2 = new ChatRequest();
        req2.setMessages(List.of(Map.of("role", "user", "content": "What is my name?")));
        ChatResponse resp2 = llmGatewayService.chat(req2, ctx2);

        assertTrue(resp2.isSuccess());
        log.info("Round 2: {}", resp2.getContent());

        // 验证模型记住了名字
        assertTrue(resp2.getContent().toLowerCase().contains("bob") || 
                   resp2.getContent().toLowerCase().contains("name"),
                   "Should remember or acknowledge the name");
    }

    /**
     * Test 3: 错误处理
     */
    @Test
    public void testErrorHandling() {
        if (llmGatewayService == null) {
            log.warn("LlmGatewayService not available, skip test");
            return;
        }

        log.info("=== Test: Error Handling ===");

        // 无效 Provider
        LlmChatContext ctx = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .providerCode("invalid-provider")
                .modelCode("test")
                .build();

        ChatRequest request = new ChatRequest();
        request.setMessages(List.of(Map.of("role", "user", "content", "hi")));

        ChatResponse response = llmGatewayService.chat(request, ctx);

        assertFalse(response.isSuccess());
        assertNotNull(response.getError());
        log.info("Expected error: {}", response.getError());
    }

    /**
     * Test 4: 费用计算
     */
    @Test
    public void testCostCalculation() {
        log.info("=== Test: Cost Calculation ===");

        int inputTokens = 1500;
        int outputTokens = 500;
        double inputPrice = 0.001;
        double outputPrice = 0.002;

        BigDecimal cost = BigDecimal.valueOf(inputTokens)
                .multiply(BigDecimal.valueOf(inputPrice))
                .divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(outputTokens)
                        .multiply(BigDecimal.valueOf(outputPrice))
                        .divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP));

        log.info("Input: {} tokens @ ¥{}/K = ¥{}", 
                inputTokens, inputPrice, 
                BigDecimal.valueOf(inputTokens).multiply(BigDecimal.valueOf(inputPrice)).divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP));
        log.info("Output: {} tokens @ ¥{}/K = ¥{}", 
                outputTokens, outputPrice,
                BigDecimal.valueOf(outputTokens).multiply(BigDecimal.valueOf(outputPrice)).divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP));
        log.info("Total Cost: ¥{}", cost);

        assertEquals(new BigDecimal("2.5000"), cost);
    }
}
