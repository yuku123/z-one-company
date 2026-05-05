package com.zifang.z.ctc.web.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "审计日志查询请求")
public class AuditLogReq {
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "操作类型")
    private String operationType;
    @Schema(description = "IP地址")
    private String ipAddress;
    @Schema(description = "当前页")
    private Integer current = 1;
    @Schema(description = "每页大小")
    private Integer pageSize = 10;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Integer getCurrent() { return current; }
    public void setCurrent(Integer current) { this.current = current; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
