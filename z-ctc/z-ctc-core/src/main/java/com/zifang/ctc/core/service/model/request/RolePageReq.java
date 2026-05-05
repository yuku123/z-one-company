package com.zifang.ctc.core.service.model.request;

import com.zifang.util.core.meta.page.PageRequest;

/**
 * 角色分页请求
 */
public class RolePageReq extends PageRequest {

    private String roleName;
    private String roleCode;
    private Integer status;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
