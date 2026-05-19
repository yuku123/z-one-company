package com.zifang.z.agent.center.core.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

@TableName("z_agent_app")
public class AgentApp {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String appCode;
    private String appName;
    private String description;
    private String iconUrl;
    private String prompt;
    private String modelName;
    private String modelProvider;
    private String tools;
    private String knowledgeIds;
    private String skillCodes;
    private String variables;
    private String status;
    private String tenantCode;
    private String creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppCode() { return appCode; }
    public void setAppCode(String appCode) { this.appCode = appCode; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getModelProvider() { return modelProvider; }
    public void setModelProvider(String modelProvider) { this.modelProvider = modelProvider; }
    public String getTools() { return tools; }
    public void setTools(String tools) { this.tools = tools; }
    public String getKnowledgeIds() { return knowledgeIds; }
    public void setKnowledgeIds(String knowledgeIds) { this.knowledgeIds = knowledgeIds; }
    public String getSkillCodes() { return skillCodes; }
    public void setSkillCodes(String skillCodes) { this.skillCodes = skillCodes; }
    public String getVariables() { return variables; }
    public void setVariables(String variables) { this.variables = variables; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTenantCode() { return tenantCode; }
    public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public LocalDateTime getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(LocalDateTime gmtCreate) { this.gmtCreate = gmtCreate; }
    public LocalDateTime getGmtModified() { return gmtModified; }
    public void setGmtModified(LocalDateTime gmtModified) { this.gmtModified = gmtModified; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
