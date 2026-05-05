package com.zifang.ctc.core.service.model.request;

import com.zifang.util.core.meta.page.PageRequest;

/**
 * 用户分页请求
 */
public class UserPageReq extends PageRequest {

    private String userName;
    private String realName;
    private String tenantCode;
    private Integer status;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
