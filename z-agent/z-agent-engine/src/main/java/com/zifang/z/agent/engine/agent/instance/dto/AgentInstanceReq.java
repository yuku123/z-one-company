package com.zifang.z.agent.engine.agent.instance.dto;

import java.io.Serializable;

public class AgentInstanceReq implements Serializable {
    private String instanceCode;
    private String appCode;
    private String userId;
    private String userName;

    public String getInstanceCode() { return instanceCode; }
    public void setInstanceCode(String instanceCode) { this.instanceCode = instanceCode; }
    public String getAppCode() { return appCode; }
    public void setAppCode(String appCode) { this.appCode = appCode; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
