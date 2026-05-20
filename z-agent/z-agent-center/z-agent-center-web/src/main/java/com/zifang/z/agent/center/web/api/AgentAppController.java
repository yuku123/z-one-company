package com.zifang.z.agent.center.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.app.dto.AgentAppDto;
import com.zifang.z.agent.center.core.app.dto.AgentAppReq;
import com.zifang.z.agent.center.core.app.service.AgentAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "Agent应用")
@RestController
@RequestMapping("/api/agent/app")
public class AgentAppController {

    @Resource
    private AgentAppService agentAppService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "分页查询应用")
    @PostMapping("/page")
    public Result<IPage<AgentAppDto>> page(@RequestBody AgentAppReq req) {
        int pageNum = req.getId() != null ? req.getId().intValue() : 1;
        int pageSize = 12;
        IPage<AgentAppDto> page = agentAppService.pageResp(req.getAppName(), req.getStatus(), pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "查询应用详情")
    @GetMapping("/get")
    public Result<AgentAppDto> get(@RequestParam String appCode) {
        return Result.success(agentAppService.getRespByAppCode(appCode));
    }

    @Operation(summary = "创建应用")
    @PostMapping
    public Result<AgentAppDto> create(@RequestBody AgentAppReq req) {
        return Result.success(agentAppService.createResp(req, objectMapper));
    }

    @Operation(summary = "更新应用")
    @PostMapping("/update")
    public Result<AgentAppDto> update(@RequestBody AgentAppReq req) {
        return Result.success(agentAppService.updateResp(req, objectMapper));
    }

    @Operation(summary = "删除应用")
    @PostMapping("/delete")
    public Result<Void> remove(@RequestParam Long id) {
        agentAppService.remove(id);
        return Result.success();
    }

    @Operation(summary = "发布应用")
    @PostMapping("/publish")
    public Result<Void> publish(@RequestParam String appCode) {
        agentAppService.publish(appCode);
        return Result.success();
    }

    @Operation(summary = "查询版本列表")
    @GetMapping("/versions")
    public Result<List<AgentAppDto>> versions(@RequestParam String appCode) {
        return Result.success(agentAppService.listVersionResp(appCode));
    }

    @Operation(summary = "获取草稿")
    @GetMapping("/draft")
    public Result<String> getDraft(@RequestParam String appCode) {
        return Result.success(agentAppService.getDraft(appCode));
    }

    @Operation(summary = "保存草稿")
    @PostMapping("/draft")
    public Result<Void> saveDraft(@RequestBody AgentAppReq req) {
        String draftJson;
        try {
            draftJson = objectMapper.writeValueAsString(req);
        } catch (Exception e) {
            draftJson = "{}";
        }
        agentAppService.saveDraft(req.getAppCode(), draftJson);
        return Result.success();
    }
}
