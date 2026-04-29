package com.zifang.z.wf.starter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 审批中心仪表盘统计数据
 */
@Schema(description = "仪表盘统计数据")
public class DashboardStatsVO {

    @Schema(description = "待我审批数量")
    private Long todoCount;

    @Schema(description = "我已审批数量")
    private Long doneCount;

    @Schema(description = "我发起的流程数量")
    private Long myProcessCount;

    @Schema(description = "抄送我的数量")
    private Long ccCount;

    public Long getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(Long todoCount) {
        this.todoCount = todoCount;
    }

    public Long getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(Long doneCount) {
        this.doneCount = doneCount;
    }

    public Long getMyProcessCount() {
        return myProcessCount;
    }

    public void setMyProcessCount(Long myProcessCount) {
        this.myProcessCount = myProcessCount;
    }

    public Long getCcCount() {
        return ccCount;
    }

    public void setCcCount(Long ccCount) {
        this.ccCount = ccCount;
    }
}
