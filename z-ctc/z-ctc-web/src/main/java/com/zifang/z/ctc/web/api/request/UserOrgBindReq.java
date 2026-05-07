package com.zifang.z.ctc.web.api.request;

public class UserOrgBindReq {
    private Long userId;
    private String deptCode;
    private String groupCode;
    private String tenantCode;
    private String domainCode;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }
    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public String getDomainCode() { return domainCode; }
    public void setDomainCode(String domainCode) { this.domainCode = domainCode; }
}
