package com.zifang.z.agent.center.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.agent.instance.dto.AgentInstanceDto;
import com.zifang.z.agent.center.core.agent.instance.service.AgentInstanceService;
import com.zifang.z.agent.center.core.agent.instance.dto.AgentInstanceReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "Agent实例")
@RestController
@RequestMapping("/api/agent/instance")
public class AgentInstanceController {

    @Resource
    private AgentInstanceService instanceService;

    @Operation(summary = "从应用创建实例")
    @PostMapping("/create")
    public Result<AgentInstanceDto> create(@RequestBody AgentInstanceReq req) {
        return Result.success(instanceService.createResp(req));
    }

    @Operation(summary = "查询实例详情")
    @GetMapping("/get")
    public Result<AgentInstanceDto> get(@RequestParam String instanceCode) {
        return Result.success(instanceService.getRespByInstanceCode(instanceCode));
    }

    @Operation(summary = "查询用户的实例列表")
    @GetMapping("/list")
    public Result<List<AgentInstanceDto>> list(@RequestParam String ownerId) {
        return Result.success(instanceService.listRespByOwner(ownerId));
    }

    @Operation(summary = "更新实例状态")
    @PostMapping("/status")
    public Result<Void> status(@RequestParam String instanceCode, @RequestParam String status) {
        instanceService.updateStatus(instanceCode, status);
        return Result.success();
    }
}
