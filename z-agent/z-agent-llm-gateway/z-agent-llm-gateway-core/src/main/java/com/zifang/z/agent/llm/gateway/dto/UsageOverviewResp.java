package com.zifang.z.agent.llm.gateway.dto;

import java.util.List;

/**
 * 用量概览响应
 */
public class UsageOverviewResp {

    private Long totalTokens;
    private Integer totalCalls;
    private Double totalCost;
    private Long todayTokens;
    private Integer todayCalls;
    private Double todayCost;
    private List<UsageStatisticsResp> topApps;
    private List<UsageStatisticsResp> topUsers;
    private List<UsageStatisticsResp> trendData;

    public Long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Long totalTokens) { this.totalTokens = totalTokens; }
    public Integer getTotalCalls() { return totalCalls; }
    public void setTotalCalls(Integer totalCalls) { this.totalCalls = totalCalls; }
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    public Long getTodayTokens() { return todayTokens; }
    public void setTodayTokens(Long todayTokens) { this.todayTokens = todayTokens; }
    public Integer getTodayCalls() { return todayCalls; }
    public void setTodayCalls(Integer todayCalls) { this.todayCalls = todayCalls; }
    public Double getTodayCost() { return todayCost; }
    public void setTodayCost(Double todayCost) { this.todayCost = todayCost; }
    public List<UsageStatisticsResp> getTopApps() { return topApps; }
    public void setTopApps(List<UsageStatisticsResp> topApps) { this.topApps = topApps; }
    public List<UsageStatisticsResp> getTopUsers() { return topUsers; }
    public void setTopUsers(List<UsageStatisticsResp> topUsers) { this.topUsers = topUsers; }
    public List<UsageStatisticsResp> getTrendData() { return trendData; }
    public void setTrendData(List<UsageStatisticsResp> trendData) { this.trendData = trendData; }
}
