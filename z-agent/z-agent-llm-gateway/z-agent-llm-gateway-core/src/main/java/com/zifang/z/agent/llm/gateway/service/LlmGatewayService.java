package com.zifang.z.agent.llm.gateway.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.z.agent.llm.gateway.dto.LlmChatContext;
import com.zifang.z.agent.llm.gateway.dto.UsageOverviewResp;
import com.zifang.z.agent.llm.gateway.dto.UsageStatisticsResp;
import com.zifang.z.agent.llm.gateway.entity.UsageConversation;
import com.zifang.z.agent.llm.gateway.entity.UsageDaily;
import com.zifang.z.agent.llm.gateway.entity.UsageRecord;
import com.zifang.z.agent.llm.gateway.mapper.UsageConversationMapper;
import com.zifang.z.agent.llm.gateway.mapper.UsageDailyMapper;
import com.zifang.z.agent.llm.gateway.mapper.UsageRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LLM Gateway Service - 统一LLM调用入口，记录用量
 */
@Service
public class LlmGatewayService {

    private static final Logger log = LoggerFactory.getLogger(LlmGatewayService.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Resource
    private UsageRecordMapper usageRecordMapper;
    @Resource
    private UsageDailyMapper usageDailyMapper;
    @Resource
    private UsageConversationMapper usageConversationMapper;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Provider配置缓存
    private final Map<String, ProviderConfig> providerConfigs = new HashMap<>();

    public LlmGatewayService() {
        // 初始化默认配置
        providerConfigs.put("ollama", new ProviderConfig("ollama", "http://localhost:11434", null));
        providerConfigs.put("openai", new ProviderConfig("openai", "https://api.openai.com/v1", System.getenv("OPENAI_API_KEY")));
    }

    /**
     * 配置Provider
     */
    public void configureProvider(String providerCode, String baseUrl, String apiKey) {
        providerConfigs.put(providerCode, new ProviderConfig(providerCode, baseUrl, apiKey));
    }

    /**
     * 统一的Chat接口 - 所有LLM调用都走这里
     */
    @Transactional
    public ChatResponse chat(ChatRequest request, LlmChatContext context) {
        long startTime = System.currentTimeMillis();
        String traceId = context.getTraceId() != null ? context.getTraceId() : UUID.randomUUID().toString().replace("-", "");

        try {
            // 1. 调用LLM
            ChatResponse response = doChat(request, context);

            // 2. 记录用量
            long latency = System.currentTimeMillis() - startTime;
            recordUsage(traceId, context, response, latency, "SUCCESS", null);

            return response;
        } catch (Exception e) {
            log.error("LLM调用失败", e);
            long latency = System.currentTimeMillis() - startTime;

            // 记录失败
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError(e.getMessage());

            recordUsage(traceId, context, errorResponse, latency, "FAILED", e.getMessage());

            return errorResponse;
        }
    }

    private ChatResponse doChat(ChatRequest request, LlmChatContext context) throws IOException {
        ProviderConfig config = providerConfigs.get(context.getProviderCode());
        if (config == null) {
            throw new IllegalArgumentException("Unknown provider: " + context.getProviderCode());
        }

        String url = config.baseUrl + "/chat/completions";

        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("model", context.getModelCode());
        reqBody.put("messages", request.getMessages());
        reqBody.put("stream", false);
        if (request.getTools() != null) {
            reqBody.put("tools", request.getTools());
        }

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(reqBody), JSON);
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json");

        if (StringUtils.isNotBlank(config.apiKey)) {
            reqBuilder.addHeader("Authorization", "Bearer " + config.apiKey);
        }

        try (Response response = httpClient.newCall(reqBuilder.build()).execute()) {
            ChatResponse chatResponse = new ChatResponse();

            if (!response.isSuccessful()) {
                chatResponse.setSuccess(false);
                chatResponse.setError("HTTP " + response.code() + ": " + response.message());
                return chatResponse;
            }

            String respBody = response.body().string();
            Map<String, Object> respMap = objectMapper.readValue(respBody, Map.class);

            chatResponse.setSuccess(true);
            chatResponse.setId((String) respMap.get("id"));
            chatResponse.setContent(extractContent(respMap));
            chatResponse.setFinishReason(extractFinishReason(respMap));
            chatResponse.setInputTokens(extractUsage(respMap, "prompt_tokens"));
            chatResponse.setOutputTokens(extractUsage(respMap, "completion_tokens"));
            chatResponse.setTotalTokens(extractUsage(respMap, "total_tokens"));
            chatResponse.setRawResponse(respMap);

            return chatResponse;
        }
    }

    private String extractContent(Map<String, Object> respMap) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message != null) {
                return (String) message.get("content");
            }
        }
        return null;
    }

    private String extractFinishReason(Map<String, Object> respMap) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
        if (choices != null && !choices.isEmpty()) {
            return (String) choices.get(0).get("finish_reason");
        }
        return null;
    }

    private int extractUsage(Map<String, Object> respMap, String key) {
        Map<String, Object> usage = (Map<String, Object>) respMap.get("usage");
        if (usage != null && usage.containsKey(key)) {
            return ((Number) usage.get(key)).intValue();
        }
        return 0;
    }

    /**
     * 记录用量
     */
    private void recordUsage(String traceId, LlmChatContext context, ChatResponse response, long latencyMs, String status, String errorMsg) {
        try {
            // 计算费用
            BigDecimal cost = BigDecimal.ZERO;
            if (response.getInputTokens() != null && response.getOutputTokens() != null) {
                Double inputPrice = context.getInputPrice() != null ? context.getInputPrice() : 0.0;
                Double outputPrice = context.getOutputPrice() != null ? context.getOutputPrice() : 0.0;
                cost = BigDecimal.valueOf(response.getInputTokens())
                        .multiply(BigDecimal.valueOf(inputPrice))
                        .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP)
                        .add(BigDecimal.valueOf(response.getOutputTokens())
                                .multiply(BigDecimal.valueOf(outputPrice))
                                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP));
            }

            // 1. 记录明细
            UsageRecord record = new UsageRecord();
            record.setTraceId(traceId);
            record.setAppCode(context.getAppCode());
            record.setInstanceCode(context.getInstanceCode());
            record.setUserId(context.getUserId());
            record.setUserName(context.getUserName());
            record.setProviderCode(context.getProviderCode());
            record.setModelCode(context.getModelCode());
            record.setInputTokens(response.getInputTokens());
            record.setOutputTokens(response.getOutputTokens());
            record.setTotalTokens(response.getTotalTokens());
            record.setLatencyMs((int) latencyMs);
            record.setStatus(status);
            record.setErrorMsg(errorMsg);
            record.setConversationCode(context.getConversationCode());
            record.setRequestId(response.getId());
            record.setInputPrice(context.getInputPrice() != null ? BigDecimal.valueOf(context.getInputPrice()) : BigDecimal.ZERO);
            record.setOutputPrice(context.getOutputPrice() != null ? BigDecimal.valueOf(context.getOutputPrice()) : BigDecimal.ZERO);
            record.setTotalCost(cost);
            record.setGmtCreate(LocalDateTime.now());
            usageRecordMapper.insert(record);

            // 2. 更新日汇总
            updateDailySummary(context, response, cost);

            // 3. 更新会话汇总
            updateConversationSummary(context, response, cost);

        } catch (Exception e) {
            log.error("记录用量失败", e);
        }
    }

    private void updateDailySummary(LlmChatContext context, ChatResponse response, BigDecimal cost) {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<UsageDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsageDaily::getStatDate, today)
                .eq(UsageDaily::getAppCode, context.getAppCode())
                .eq(UsageDaily::getUserId, context.getUserId())
                .eq(UsageDaily::getProviderCode, context.getProviderCode())
                .eq(UsageDaily::getModelCode, context.getModelCode());

        UsageDaily daily = usageDailyMapper.selectOne(wrapper);

        if (daily == null) {
            daily = new UsageDaily();
            daily.setStatDate(today);
            daily.setAppCode(context.getAppCode());
            daily.setUserId(context.getUserId());
            daily.setProviderCode(context.getProviderCode());
            daily.setModelCode(context.getModelCode());
            daily.setTotalCalls(1);
            daily.setTotalInputTokens((long) (response.getInputTokens() != null ? response.getInputTokens() : 0));
            daily.setTotalOutputTokens((long) (response.getOutputTokens() != null ? response.getOutputTokens() : 0));
            daily.setTotalTokens((long) (response.getTotalTokens() != null ? response.getTotalTokens() : 0));
            daily.setTotalCost(cost);
            daily.setAvgLatencyMs(response.getLatencyMs());
            daily.setSuccessCalls("SUCCESS".equals(response.getStatus()) ? 1 : 0);
            daily.setFailedCalls("SUCCESS".equals(response.getStatus()) ? 0 : 1);
            usageDailyMapper.insert(daily);
        } else {
            daily.setTotalCalls(daily.getTotalCalls() + 1);
            daily.setTotalInputTokens(daily.getTotalInputTokens() + (response.getInputTokens() != null ? response.getInputTokens() : 0));
            daily.setTotalOutputTokens(daily.getTotalOutputTokens() + (response.getOutputTokens() != null ? response.getOutputTokens() : 0));
            daily.setTotalTokens(daily.getTotalTokens() + (response.getTotalTokens() != null ? response.getTotalTokens() : 0));
            daily.setTotalCost(daily.getTotalCost().add(cost));
            int totalLatency = daily.getAvgLatencyMs() * (daily.getTotalCalls() - 1) + (response.getLatencyMs() != null ? response.getLatencyMs() : 0);
            daily.setAvgLatencyMs(totalLatency / daily.getTotalCalls());
            if ("SUCCESS".equals(response.getStatus())) {
                daily.setSuccessCalls(daily.getSuccessCalls() + 1);
            } else {
                daily.setFailedCalls(daily.getFailedCalls() + 1);
            }
            usageDailyMapper.updateById(daily);
        }
    }

    private void updateConversationSummary(LlmChatContext context, ChatResponse response, BigDecimal cost) {
        if (context.getConversationCode() == null) return;

        LambdaQueryWrapper<UsageConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsageConversation::getConversationCode, context.getConversationCode());
        UsageConversation conv = usageConversationMapper.selectOne(wrapper);

        if (conv == null) {
            conv = new UsageConversation();
            conv.setConversationCode(context.getConversationCode());
            conv.setAppCode(context.getAppCode());
            conv.setInstanceCode(context.getInstanceCode());
            conv.setUserId(context.getUserId());
            conv.setUserName(context.getUserName());
            conv.setProviderCode(context.getProviderCode());
            conv.setModelCode(context.getModelCode());
            conv.setTotalCalls(1);
            conv.setTotalInputTokens((long) (response.getInputTokens() != null ? response.getInputTokens() : 0));
            conv.setTotalOutputTokens((long) (response.getOutputTokens() != null ? response.getOutputTokens() : 0));
            conv.setTotalTokens((long) (response.getTotalTokens() != null ? response.getTotalTokens() : 0));
            conv.setTotalCost(cost);
            conv.setFirstMessage(response.getInputMessage());
            conv.setLastMessage(response.getContent());
            conv.setGmtCreate(LocalDateTime.now());
            usageConversationMapper.insert(conv);
        } else {
            conv.setTotalCalls(conv.getTotalCalls() + 1);
            conv.setTotalInputTokens(conv.getTotalInputTokens() + (response.getInputTokens() != null ? response.getInputTokens() : 0));
            conv.setTotalOutputTokens(conv.getTotalOutputTokens() + (response.getOutputTokens() != null ? response.getOutputTokens() : 0));
            conv.setTotalTokens(conv.getTotalTokens() + (response.getTotalTokens() != null ? response.getTotalTokens() : 0));
            conv.setTotalCost(conv.getTotalCost().add(cost));
            conv.setLastMessage(response.getContent());
            conv.setGmtModified(LocalDateTime.now());
            usageConversationMapper.updateById(conv);
        }
    }

    /**
     * 获取用量概览
     */
    public UsageOverviewResp getOverview(String appCode, String userId, String startDate, String endDate) {
        UsageOverviewResp resp = new UsageOverviewResp();

        // 总览
        LambdaQueryWrapper<UsageDaily> totalWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(appCode)) totalWrapper.eq(UsageDaily::getAppCode, appCode);
        if (StringUtils.isNotBlank(userId)) totalWrapper.eq(UsageDaily::getUserId, userId);
        List<UsageDaily> allDaily = usageDailyMapper.selectList(totalWrapper);

        long totalTokens = allDaily.stream().mapToLong(UsageDaily::getTotalTokens).sum();
        int totalCalls = allDaily.stream().mapToInt(UsageDaily::getTotalCalls).sum();
        double totalCost = allDaily.stream().mapToDouble(d -> d.getTotalCost().doubleValue()).sum();

        resp.setTotalTokens(totalTokens);
        resp.setTotalCalls(totalCalls);
        resp.setTotalCost(totalCost);

        // 今日
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<UsageDaily> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(UsageDaily::getStatDate, today);
        if (StringUtils.isNotBlank(appCode)) todayWrapper.eq(UsageDaily::getAppCode, appCode);
        if (StringUtils.isNotBlank(userId)) todayWrapper.eq(UsageDaily::getUserId, userId);
        List<UsageDaily> todayDaily = usageDailyMapper.selectList(todayWrapper);

        long todayTokens = todayDaily.stream().mapToLong(UsageDaily::getTotalTokens).sum();
        int todayCalls = todayDaily.stream().mapToInt(UsageDaily::getTotalCalls).sum();
        double todayCost = todayDaily.stream().mapToDouble(d -> d.getTotalCost().doubleValue()).sum();

        resp.setTodayTokens(todayTokens);
        resp.setTodayCalls(todayCalls);
        resp.setTodayCost(todayCost);

        return resp;
    }

    /**
     * 按App统计用量
     */
    public List<UsageStatisticsResp> getUsageByApp(String startDate, String endDate) {
        LambdaQueryWrapper<UsageDaily> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(startDate)) {
            wrapper.ge(UsageDaily::getStatDate, LocalDate.parse(startDate));
        }
        if (StringUtils.isNotBlank(endDate)) {
            wrapper.le(UsageDaily::getStatDate, LocalDate.parse(endDate));
        }

        List<UsageDaily> dailyList = usageDailyMapper.selectList(wrapper);

        return dailyList.stream()
                .collect(Collectors.groupingBy(UsageDaily::getAppCode))
                .entrySet().stream()
                .map(entry -> {
                    UsageStatisticsResp stat = new UsageStatisticsResp();
                    stat.setAppCode(entry.getKey());
                    List<UsageDaily> dailies = entry.getValue();
                    stat.setTotalCalls(dailies.stream().mapToInt(UsageDaily::getTotalCalls).sum());
                    stat.setInputTokens(dailies.stream().mapToLong(UsageDaily::getTotalInputTokens).sum());
                    stat.setOutputTokens(dailies.stream().mapToLong(UsageDaily::getTotalOutputTokens).sum());
                    stat.setTotalTokens(dailies.stream().mapToLong(UsageDaily::getTotalTokens).sum());
                    stat.setTotalCost(BigDecimal.valueOf(dailies.stream().mapToDouble(d -> d.getTotalCost().doubleValue()).sum()));
                    return stat;
                })
                .sorted((a, b) -> Long.compare(b.getTotalTokens(), a.getTotalTokens()))
                .collect(Collectors.toList());
    }

    /**
     * 按用户统计用量
     */
    public List<UsageStatisticsResp> getUsageByUser(String appCode, String startDate, String endDate) {
        LambdaQueryWrapper<UsageDaily> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(appCode)) wrapper.eq(UsageDaily::getAppCode, appCode);
        if (StringUtils.isNotBlank(startDate)) wrapper.ge(UsageDaily::getStatDate, LocalDate.parse(startDate));
        if (StringUtils.isNotBlank(endDate)) wrapper.le(UsageDaily::getStatDate, LocalDate.parse(endDate));

        List<UsageDaily> dailyList = usageDailyMapper.selectList(wrapper);

        return dailyList.stream()
                .collect(Collectors.groupingBy(UsageDaily::getUserId))
                .entrySet().stream()
                .map(entry -> {
                    UsageStatisticsResp stat = new UsageStatisticsResp();
                    stat.setUserId(entry.getKey());
                    List<UsageDaily> dailies = entry.getValue();
                    stat.setTotalCalls(dailies.stream().mapToInt(UsageDaily::getTotalCalls).sum());
                    stat.setTotalTokens(dailies.stream().mapToLong(UsageDaily::getTotalTokens).sum());
                    stat.setTotalCost(BigDecimal.valueOf(dailies.stream().mapToDouble(d -> d.getTotalCost().doubleValue()).sum()));
                    return stat;
                })
                .sorted((a, b) -> Long.compare(b.getTotalTokens(), a.getTotalTokens()))
                .collect(Collectors.toList());
    }

    /**
     * 获取每日趋势
     */
    public List<UsageStatisticsResp> getUsageTrend(String appCode, String userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        LambdaQueryWrapper<UsageDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(UsageDaily::getStatDate, startDate).le(UsageDaily::getStatDate, endDate);
        if (StringUtils.isNotBlank(appCode)) wrapper.eq(UsageDaily::getAppCode, appCode);
        if (StringUtils.isNotBlank(userId)) wrapper.eq(UsageDaily::getUserId, userId);
        wrapper.orderByAsc(UsageDaily::getStatDate);

        List<UsageDaily> dailyList = usageDailyMapper.selectList(wrapper);

        return dailyList.stream()
                .collect(Collectors.groupingBy(UsageDaily::getStatDate))
                .entrySet().stream()
                .map(entry -> {
                    UsageStatisticsResp stat = new UsageStatisticsResp();
                    stat.setDate(entry.getKey().toString());
                    List<UsageDaily> dailies = entry.getValue();
                    stat.setDailyCalls(dailies.stream().mapToInt(UsageDaily::getTotalCalls).sum());
                    stat.setDailyTokens(dailies.stream().mapToLong(UsageDaily::getTotalTokens).sum());
                    return stat;
                })
                .sorted(Comparator.comparing(UsageStatisticsResp::getDate))
                .collect(Collectors.toList());
    }

    // 内部类
    private static class ProviderConfig {
        String providerCode;
        String baseUrl;
        String apiKey;

        ProviderConfig(String providerCode, String baseUrl, String apiKey) {
            this.providerCode = providerCode;
            this.baseUrl = baseUrl;
            this.apiKey = apiKey;
        }
    }

    /**
     * Chat请求
     */
    public static class ChatRequest {
        private List<Map<String, String>> messages;
        private List<Map<String, Object>> tools;

        public List<Map<String, String>> getMessages() { return messages; }
        public void setMessages(List<Map<String, String>> messages) { this.messages = messages; }
        public List<Map<String, Object>> getTools() { return tools; }
        public void setTools(List<Map<String, Object>> tools) { this.tools = tools; }
    }

    /**
     * Chat响应
     */
    public static class ChatResponse {
        private boolean success;
        private String id;
        private String content;
        private String finishReason;
        private Integer inputTokens;
        private Integer outputTokens;
        private Integer totalTokens;
        private Integer latencyMs;
        private String error;
        private String status;
        private String inputMessage;
        private Map<String, Object> rawResponse;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
        public Integer getInputTokens() { return inputTokens; }
        public void setInputTokens(Integer inputTokens) { this.inputTokens = inputTokens; }
        public Integer getOutputTokens() { return outputTokens; }
        public void setOutputTokens(Integer outputTokens) { this.outputTokens = outputTokens; }
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
        public Integer getLatencyMs() { return latencyMs; }
        public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getInputMessage() { return inputMessage; }
        public void setInputMessage(String inputMessage) { this.inputMessage = inputMessage; }
        public Map<String, Object> getRawResponse() { return rawResponse; }
        public void setRawResponse(Map<String, Object> rawResponse) { this.rawResponse = rawResponse; }
    }
}
