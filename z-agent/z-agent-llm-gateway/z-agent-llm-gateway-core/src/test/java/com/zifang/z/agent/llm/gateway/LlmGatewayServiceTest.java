package com.zifang.z.agent.llm.gateway;

import com.zifang.z.agent.llm.gateway.dto.LlmChatContext;
import com.zifang.z.agent.llm.gateway.dto.UsageOverviewResp;
import com.zifang.z.agent.llm.gateway.entity.UsageRecord;
import com.zifang.z.agent.llm.gateway.mapper.UsageRecordMapper;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService.ChatRequest;
import com.zifang.z.agent.llm.gateway.service.LlmGatewayService.ChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LlmGateway 完整链路测试
 * 
 * 测试链路：
 * 1. 配置 Ollama Provider
 * 2. 调用 Chat 接口
 * 3. 验证用量记录
 * 4. 查询统计数据
 */
@SpringBootTest
public class LlmGatewayServiceTest {

    @Autowired
    private LlmGatewayService llmGatewayService;

    @Autowired
    private UsageRecordMapper usageRecordMapper;

    /**
     * Test 1: 直接调用 Ollama Chat
     */
    @Test
    public void testDirectChatWithOllama() {
        // 配置 Ollama
        llmGatewayService.configureProvider("ollama", "http://localhost:11434", null);

        // 构建上下文
        LlmChatContext ctx = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .appCode("test-app")
                .instanceCode("test-instance")
                .userId("user001")
                .userName("测试用户")
                .conversationCode("conv-001")
                .providerCode("ollama")
                .modelCode("qwen2.5:7b-instruct")
                .inputPrice(0.0)  // 本地模型免费
                .outputPrice(0.0)
                .build();

        // 构建请求
        ChatRequest request = new ChatRequest();
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", "你好，请介绍一下你自己"));
        request.setMessages(messages);

        // 调用
        System.out.println("=== 开始调用 Ollama ===");
        long startTime = System.currentTimeMillis();
        ChatResponse response = llmGatewayService.chat(request, ctx);
        long costTime = System.currentTimeMillis() - startTime;

        // 验证
        System.out.println("调用耗时: " + costTime + "ms");
        System.out.println("成功: " + response.isSuccess());
        System.out.println("内容: " + response.getContent());
        System.out.println("Input Tokens: " + response.getInputTokens());
        System.out.println("Output Tokens: " + response.getOutputTokens());
        System.out.println("Total Tokens: " + response.getTotalTokens());

        assertTrue(response.isSuccess(), "调用应该成功");
        assertNotNull(response.getContent(), "响应内容不应为空");
        assertTrue(costTime < 120000, "调用应在2分钟内完成");
        
        System.out.println("=== Ollama 调用测试通过 ===\n");
    }

    /**
     * Test 2: 多轮对话
     */
    @Test
    public void testMultiTurnConversation() {
        llmGatewayService.configureProvider("ollama", "http://localhost:11434", null);

        LlmChatContext ctx = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .appCode("test-app")
                .userId("user001")
                .conversationCode("conv-multi-001")
                .providerCode("ollama")
                .modelCode("qwen2.5:7b-instruct")
                .build();

        // 第一轮
        ChatRequest req1 = new ChatRequest();
        req1.setMessages(List.of(
                Map.of("role", "user", "content", "我是张三")
        ));
        ChatResponse resp1 = llmGatewayService.chat(req1, ctx);
        assertTrue(resp1.isSuccess());
        System.out.println("第一轮: " + resp1.getContent().substring(0, Math.min(100, resp1.getContent().length())) + "...");

        // 第二轮 - 带历史
        ChatRequest req2 = new ChatRequest();
        req2.setMessages(List.of(
                Map.of("role", "user", "content", "我叫啥？")
        ));
        ChatResponse resp2 = llmGatewayService.chat(req2, ctx);
        assertTrue(resp2.isSuccess());
        System.out.println("第二轮: " + resp2.getContent());

        System.out.println("=== 多轮对话测试通过 ===\n");
    }

    /**
     * Test 3: 验证用量记录
     */
    @Test
    public void testUsageRecord() {
        llmGatewayService.configureProvider("ollama", "http://localhost:11434", null);

        LlmChatContext ctx = LlmChatContext.builder()
                .traceId(UUID.randomUUID().toString())
                .appCode("test-app")
                .userId("user001")
                .conversationCode("conv-usage-001")
                .providerCode("ollama")
                .modelCode("qwen2.5:7b-instruct")
                .inputPrice(0.001)
                .outputPrice(0.002)
                .build();

        ChatRequest request = new ChatRequest();
        request.setMessages(List.of(
                Map.of("role", "user", "content", "说个笑话")
        ));

        // 调用
        ChatResponse response = llmGatewayService.chat(request, ctx);
        assertTrue(response.isSuccess());

        // 查询记录
        UsageRecord record = usageRecordMapper.selectList(null).stream()
                .filter(r -> r.getTraceId().equals(ctx.getTraceId()))
                .findFirst()
                .orElse(null);

        assertNotNull(record, "应该有用量记录");
        System.out.println("用量记录:");
        System.out.println("  App: " + record.getAppCode());
        System.out.println("  User: " + record.getUserName());
        System.out.println("  Model: " + record.getModelCode());
        System.out.println("  Input Tokens: " + record.getInputTokens());
        System.out.println("  Output Tokens: " + record.getOutputTokens());
        System.out.println("  Total Tokens: " + record.getTotalTokens());
        System.out.println("  Latency: " + record.getLatencyMs() + "ms");
        System.out.println("  Cost: ¥" + record.getTotalCost());
        System.out.println("  Status: " + record.getStatus());

        assertEquals("test-app", record.getAppCode());
        assertEquals("user001", record.getUserId());
        assertEquals("ollama", record.getProviderCode());
        assertEquals("SUCCESS", record.getStatus());

        System.out.println("=== 用量记录测试通过 ===\n");
    }

    /**
     * Test 4: 获取用量统计
     */
    @Test
    public void testUsageStatistics() {
        // 先做几次调用
        testDirectChatWithOllama();

        // 查询概览
        UsageOverviewResp overview = llmGatewayService.getOverview("test-app", null, null, null);
        System.out.println("用量概览:");
        System.out.println("  累计 Tokens: " + overview.getTotalTokens());
        System.out.println("  累计 Calls: " + overview.getTotalCalls());
        System.out.println("  累计 Cost: ¥" + overview.getTotalCost());

        // 按 App 统计
        var byApp = llmGatewayService.getUsageByApp(null, null);
        System.out.println("\n按 App 统计:");
        byApp.forEach(app -> {
            System.out.println("  " + app.getAppCode() + ": " + app.getTotalTokens() + " tokens");
        });

        // 按用户统计
        var byUser = llmGatewayService.getUsageByUser("test-app", null, null);
        System.out.println("\n按用户统计:");
        byUser.forEach(user -> {
            System.out.println("  " + user.getUserId() + ": " + user.getTotalTokens() + " tokens");
        });

        // 趋势
        var trend = llmGatewayService.getUsageTrend("test-app", null, 7);
        System.out.println("\n7天趋势:");
        trend.forEach(t -> {
            System.out.println("  " + t.getDate() + ": " + t.getDailyTokens() + " tokens, " + t.getDailyCalls() + " calls");
        });

        System.out.println("\n=== 用量统计测试通过 ===");
    }

    /**
     * Test 5: 错误处理 - 无效 Provider
     */
    @Test
    public void testInvalidProvider() {
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
        System.out.println("预期错误: " + response.getError());
        System.out.println("=== 错误处理测试通过 ===");
    }
}
