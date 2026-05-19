package com.zifang.z.agent.center.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.agent.instance.entity.AgentInstance;
import com.zifang.z.agent.center.core.agent.instance.service.AgentInstanceService;
import com.zifang.z.agent.center.web.api.request.AgentInstanceReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Agent实例")
@RestController
@RequestMapping("/api/agent/instance")
public class AgentInstanceController {

    @Resource
    private AgentInstanceService instanceService;

    @Operation(summary = "从应用创建实例")
    @PostMapping("/create")
    public Result<AgentInstanceResp> create(@RequestBody AgentInstanceReq req) {
        AgentInstance instance = instanceService.createFromApp(req.getAppCode(), req.getUserId(), req.getUserName());
        return Result.success(toResp(instance));
    }

    @Operation(summary = "查询实例详情")
    @GetMapping("/get")
    public Result<AgentInstanceResp> get(@RequestParam String instanceCode) {
        AgentInstance instance = instanceService.getByInstanceCode(instanceCode);
        return Result.success(toResp(instance));
    }

    @Operation(summary = "查询用户的实例列表")
    @GetMapping("/list")
    public Result<List<AgentInstanceResp>> list(@RequestParam String ownerId) {
        List<AgentInstance> list = instanceService.listByOwner(ownerId);
        return Result.success(list.stream().map(this::toResp).collect(Collectors.toList()));
    }

    @Operation(summary = "启用/停用实例")
    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam String instanceCode, @RequestParam String status) {
        instanceService.updateStatus(instanceCode, status);
        return Result.success();
    }

    private AgentInstanceResp toResp(AgentInstance instance) {
        if (instance == null) return null;
        AgentInstanceResp resp = new AgentInstanceResp();
        BeanUtils.copyProperties(instance, resp);
        return resp;
    }

    public static class AgentInstanceResp {
        private String instanceCode;
        private String appCode;
        private String appVersion;
        private String instanceName;
        private String ownerId;
        private String ownerName;
        private String status;
        private Integer visitCount;
        private String lastVisitTime;

        public String getInstanceCode() { return instanceCode; }
        public void setInstanceCode(String instanceCode) { this.instanceCode = instanceCode; }
        public String getAppCode() { return appCode; }
        public void setAppCode(String appCode) { this.appCode = appCode; }
        public String getAppVersion() { return appVersion; }
        public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
        public String getInstanceName() { return instanceName; }
        public void setInstanceName(String instanceName) { this.instanceName = instanceName; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public String getOwnerName() { return ownerName; }
        public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getVisitCount() { return visitCount; }
        public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }
        public String getLastVisitTime() { return lastVisitTime; }
        public void setLastVisitTime(String lastVisitTime) { this.lastVisitTime = lastVisitTime; }
    }
}
