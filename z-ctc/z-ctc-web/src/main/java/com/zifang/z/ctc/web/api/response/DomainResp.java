package com.zifang.z.ctc.web.api.response;

public class DomainResp {
    private String domainCode;
    private String domainName;
    private String tenantCode;
    private Integer status;
    private String description;
    private String extConfig;

    public String getDomainCode() { return domainCode; }
    public void setDomainCode(String domainCode) { this.domainCode = domainCode; }
    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getExtConfig() { return extConfig; }
    public void setExtConfig(String extConfig) { this.extConfig = extConfig; }
}
