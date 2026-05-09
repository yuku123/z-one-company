package com.zifang.z.agent.skill.core.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SkillCategoryDTO {

    private Long id;
    private String catCode;
    private String catName;
    private String parentCode;
    private Integer sortOrder;
    private LocalDateTime gmtCreate;
    private List<SkillCategoryDTO> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatCode() {
        return catCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public List<SkillCategoryDTO> getChildren() {
        return children;
    }

    public void setChildren(List<SkillCategoryDTO> children) {
        this.children = children;
    }

}
