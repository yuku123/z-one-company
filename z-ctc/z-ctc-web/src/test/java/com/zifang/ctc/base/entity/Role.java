package com.zifang.ctc.base.entity;

import lombok.Data;

/**
 * @author miemie
 * @since 2018-08-10
 */
public class Role {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 角色名
     */
    private String roleName;
    /**
     * 角色描述
     */
    private String roleDescribe;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public String getRoleDescribe() {
        return roleDescribe;
    }
    public void setRoleDescribe(String roleDescribe) {
        this.roleDescribe = roleDescribe;
    }
}
