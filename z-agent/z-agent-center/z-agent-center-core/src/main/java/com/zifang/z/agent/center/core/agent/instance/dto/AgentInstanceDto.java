package com.zifang.z.agent.center.core.agent.instance.dto;

import java.io.Serializable;

public class AgentInstanceDto implements Serializable {
    private Long id;
    private String instanceCode;
    private String appCode;
    private String appVersion;
    private String instanceName;
    private String ownerId;
    private String ownerName;
    private String status;
    private Integer visitCount;
    private String lastVisitTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInstanceCode() { return instanceCode; }
    public void setInstanceCode(String instanceCode) { this.instanceCode = instanceCode; }
    public String getAppCode() { return appCode; }
    public void setAppCode(String appCode) { this.appCode = appCode; }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    public String getInstanceName() { return instanceName; }
    public void setInstanceName(String instanceName) { this.instanceName = instanceName; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getVisitCount() { return visitCount; }
    public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }
    public String getLastVisitTime() { return lastVisitTime; }
    public void setLastVisitTime(String lastVisitTime) { this.lastVisitTime = lastVisitTime; }
}
