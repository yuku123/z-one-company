package com.zifang.ctc.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("z_ctc_dictionary")
public class Dictionary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String category;
    private String dictKey;
    private String dictValue;
    private String tenantCode;
    private String domainCode;
    private Integer sortOrder;
    private Integer isBuiltin;
    private Long parentId;
    private Integer status;
    private String description;
    private String extSchema;
    private String extData;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDictKey() { return dictKey; }
    public void setDictKey(String dictKey) { this.dictKey = dictKey; }
    public String getDictValue() { return dictValue; }
    public void setDictValue(String dictValue) { this.dictValue = dictValue; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public String getDomainCode() { return domainCode; }
    public void setDomainCode(String domainCode) { this.domainCode = domainCode; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getIsBuiltin() { return isBuiltin; }
    public void setIsBuiltin(Integer isBuiltin) { this.isBuiltin = isBuiltin; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getExtSchema() { return extSchema; }
    public void setExtSchema(String extSchema) { this.extSchema = extSchema; }
    public String getExtData() { return extData; }
    public void setExtData(String extData) { this.extData = extData; }
    public LocalDateTime getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(LocalDateTime gmtCreate) { this.gmtCreate = gmtCreate; }
    public LocalDateTime getGmtModified() { return gmtModified; }
    public void setGmtModified(LocalDateTime gmtModified) { this.gmtModified = gmtModified; }
}
