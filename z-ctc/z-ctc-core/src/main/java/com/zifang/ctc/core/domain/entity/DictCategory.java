package com.zifang.ctc.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("z_ctc_dict_category")
public class DictCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String catCode;
    private String catName;
    private String parentCode;
    private String tenantCode;
    private Integer sortOrder;
    private LocalDateTime gmtCreate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCatCode() { return catCode; }
    public void setCatCode(String catCode) { this.catCode = catCode; }
    public String getCatName() { return catName; }
    public void setCatName(String catName) { this.catName = catName; }
    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(LocalDateTime gmtCreate) { this.gmtCreate = gmtCreate; }
}
