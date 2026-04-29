package com.zifang.z.wf.starter.api;


/**
 * 待办任务DTO：仅封装业务需要的字段，避免序列化Camunda底层实体的关联属性
 */
public class TodoTaskDTO {
    // 任务ID（核心字段，用于完成审批）
    private String taskId;
    // 任务名称（如“审批请假”）
    private String taskName;
    // 审批人
    private String assignee;
    // 流程实例ID
    private String processInstanceId;
    // 流程定义ID（如leaveProcess:1:xxx）
    private String processDefinitionId;
    // 流程定义名称（如“员工请假流程”）
    private String processDefinitionName;
    // 任务创建时间
    private String createTime;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}