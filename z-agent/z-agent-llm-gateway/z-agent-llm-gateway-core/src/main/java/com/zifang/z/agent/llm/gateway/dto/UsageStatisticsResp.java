package com.zifang.z.agent.llm.gateway.dto;

import java.math.BigDecimal;

/**
 * 用量统计响应
 */
public class UsageStatisticsResp {

    private String appCode;
    private String appName;
    private String userId;
    private String userName;
    private Integer totalCalls;
    private Long inputTokens;
    private Long outputTokens;
    private Long totalTokens;
    private BigDecimal totalCost;
    private Double avgLatencyMs;

    // 趋势数据
    private String date;
    private Long dailyTokens;
    private Integer dailyCalls;

    public String getAppCode() { return appCode; }
    public void setAppCode(String appCode) { this.appCode = appCode; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getTotalCalls() { return totalCalls; }
    public void setTotalCalls(Integer totalCalls) { this.totalCalls = totalCalls; }
    public Long getInputTokens() { return inputTokens; }
    public void setInputTokens(Long inputTokens) { this.inputTokens = inputTokens; }
    public Long getOutputTokens() { return outputTokens; }
    public void setOutputTokens(Long outputTokens) { this.outputTokens = outputTokens; }
    public Long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Long totalTokens) { this.totalTokens = totalTokens; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public Double getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(Double avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getDailyTokens() { return dailyTokens; }
    public void setDailyTokens(Long dailyTokens) { this.dailyTokens = dailyTokens; }
    public Integer getDailyCalls() { return dailyCalls; }
    public void setDailyCalls(Integer dailyCalls) { this.dailyCalls = dailyCalls; }
}
