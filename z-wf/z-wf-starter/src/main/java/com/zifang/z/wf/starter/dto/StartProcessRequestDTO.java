package com.zifang.z.wf.starter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.Objects;

/**
 * 启动流程请求DTO
 */
@Schema(description = "启动流程请求")
public class StartProcessRequestDTO {

    @Schema(description = "流程定义Key")
    private String processKey;

    @Schema(description = "业务Key")
    private String businessKey;

    @Schema(description = "发起人/申请人")
    private String initiator;

    @Schema(description = "流程标题/摘要")
    private String title;

    @Schema(description = "流程变量/表单数据")
    private Map<String, Object> variables;

    public StartProcessRequestDTO() {
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartProcessRequestDTO that = (StartProcessRequestDTO) o;
        return Objects.equals(processKey, that.processKey) && Objects.equals(businessKey, that.businessKey) && Objects.equals(initiator, that.initiator) && Objects.equals(title, that.title) && Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processKey, businessKey, initiator, title, variables);
    }

    @Override
    public String toString() {
        return "StartProcessRequestDTO{" +
                "processKey='" + processKey + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", initiator='" + initiator + '\'' +
                ", title='" + title + '\'' +
                ", variables=" + variables +
                '}';
    }
}
