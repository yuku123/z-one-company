package com.zifang.ctc.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("z_ctc_tenant")
public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tenantCode;
    private String tenantName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer status;
    private LocalDateTime expireTime;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantCode() {
        return tenantCode;
    }
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
    public String getTenantName() {
        return tenantName;
    }
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public String getContactPhone() {
        return contactPhone;
    }
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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
