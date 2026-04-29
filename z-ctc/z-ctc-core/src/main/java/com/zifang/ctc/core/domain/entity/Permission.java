package com.zifang.ctc.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体 - 4A授权管理
 */
@TableName("z_ctc_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限编码
     */
    @TableField("perm_code")
    private String permCode;

    /**
     * 权限名称
     */
    @TableField("perm_name")
    private String permName;

    /**
     * 权限类型：MENU-菜单，BUTTON-按钮，API-接口
     */
    @TableField("perm_type")
    private String permType;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 权限路径
     */
    @TableField("path")
    private String path;

    /**
     * 排序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 租户编码
     */
    @TableField("tenant_code")
    private String tenantCode;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPermCode() {
        return permCode;
    }
    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }
    public String getPermName() {
        return permName;
    }
    public void setPermName(String permName) {
        this.permName = permName;
    }
    public String getPermType() {
        return permType;
    }
    public void setPermType(String permType) {
        this.permType = permType;
    }
    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Integer getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}
