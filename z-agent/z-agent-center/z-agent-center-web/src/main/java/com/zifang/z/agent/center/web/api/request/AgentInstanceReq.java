package com.zifang.z.agent.center.web.api.request;

public class AgentInstanceReq {
    private String appCode;
    private String userId;
    private String userName;

    public String getAppCode() { return appCode; }
    public void setAppCode(String appCode) { this.appCode = appCode; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
