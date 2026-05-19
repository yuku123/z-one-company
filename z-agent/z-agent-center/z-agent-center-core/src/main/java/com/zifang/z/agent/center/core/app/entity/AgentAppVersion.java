package com.zifang.z.agent.center.core.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("z_agent_app_version")
public class AgentAppVersion {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String appCode;
    private String version;
    private String prompt;
    private String modelName;
    private String modelProvider;
    private String tools;
    private String knowledgeIds;
    private String skillCodes;
    private String variables;
    private String changeLog;
    private String status;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppCode() { return appCode; }
    public void setAppCode(String appCode) { this.appCode = appCode; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
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
    public String getChangeLog() { return changeLog; }
    public void setChangeLog(String changeLog) { this.changeLog = changeLog; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(LocalDateTime gmtCreate) { this.gmtCreate = gmtCreate; }
    public LocalDateTime getGmtModified() { return gmtModified; }
    public void setGmtModified(LocalDateTime gmtModified) { this.gmtModified = gmtModified; }
}
