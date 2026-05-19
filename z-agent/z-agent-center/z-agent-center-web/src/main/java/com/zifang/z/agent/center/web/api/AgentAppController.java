package com.zifang.z.agent.center.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.app.entity.AgentApp;
import com.zifang.z.agent.center.core.app.entity.AgentAppVersion;
import com.zifang.z.agent.center.core.app.service.AgentAppService;
import com.zifang.z.agent.center.web.api.request.AgentAppReq;
import com.zifang.z.agent.center.web.api.response.AgentAppResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Agent应用")
@RestController
@RequestMapping("/api/agent/app")
public class AgentAppController {

    @Resource
    private AgentAppService agentAppService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "分页查询应用")
    @PostMapping("/page")
    public Result<IPage<AgentAppResp>> page(@RequestBody AgentAppReq req) {
        int pageNum = req.getId() != null ? req.getId().intValue() : 1;
        int pageSize = 12;
        IPage<AgentApp> page = agentAppService.page(req.getAppName(), req.getStatus(), pageNum, pageSize);
        return Result.success(page.convert(this::toResp));
    }

    @Operation(summary = "查询应用详情")
    @GetMapping("/get")
    public Result<AgentAppResp> get(@RequestParam String appCode) {
        AgentApp app = agentAppService.getByAppCode(appCode);
        return Result.success(toResp(app));
    }

    @Operation(summary = "创建应用")
    @PostMapping
    public Result<AgentAppResp> create(@RequestBody AgentAppReq req) {
        AgentApp app = toEntity(req);
        AgentApp created = agentAppService.create(app);
        return Result.success(toResp(created));
    }

    @Operation(summary = "更新应用")
    @PostMapping("/update")
    public Result<AgentAppResp> update(@RequestBody AgentAppReq req) {
        AgentApp app = toEntity(req);
        if (app.getId() == null && app.getAppCode() != null) {
            AgentApp existing = agentAppService.getByAppCode(app.getAppCode());
            if (existing != null) {
                app.setId(existing.getId());
            }
        }
        AgentApp updated = agentAppService.update(app);
        return Result.success(toResp(updated));
    }

    @Operation(summary = "删除应用")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        agentAppService.delete(id);
        return Result.success();
    }

    @Operation(summary = "发布应用")
    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody AgentAppReq req) {
        agentAppService.publish(req.getAppCode());
        return Result.success();
    }

    @Operation(summary = "查询版本列表")
    @GetMapping("/versions")
    public Result<List<AgentAppResp>> versions(@RequestParam String appCode) {
        List<AgentAppVersion> list = agentAppService.listVersions(appCode);
        return Result.success(list.stream().map(v -> {
            AgentAppResp resp = new AgentAppResp();
            resp.setAppCode(v.getAppCode());
            resp.setGmtCreate(v.getGmtCreate() != null ? v.getGmtCreate().format(DF) : null);
            return resp;
        }).collect(Collectors.toList()));
    }

    @Operation(summary = "获取草稿")
    @GetMapping("/draft")
    public Result<String> getDraft(@RequestParam String appCode) {
        String draft = agentAppService.getDraft(appCode);
        return Result.success(draft);
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

    private AgentAppResp toResp(AgentApp app) {
        if (app == null) return null;
        AgentAppResp resp = new AgentAppResp();
        BeanUtils.copyProperties(app, resp);
        resp.setGmtCreate(app.getGmtCreate() != null ? app.getGmtCreate().format(DF) : null);
        resp.setGmtModified(app.getGmtModified() != null ? app.getGmtModified().format(DF) : null);
        return resp;
    }

    private AgentApp toEntity(AgentAppReq req) {
        AgentApp app = new AgentApp();
        BeanUtils.copyProperties(req, app);
        if (req.getTools() != null) {
            try { app.setTools(objectMapper.writeValueAsString(req.getTools())); } catch (Exception e) {}
        }
        if (req.getKnowledgeIds() != null) {
            try { app.setKnowledgeIds(objectMapper.writeValueAsString(req.getKnowledgeIds())); } catch (Exception e) {}
        }
        if (req.getSkillCodes() != null) {
            try { app.setSkillCodes(objectMapper.writeValueAsString(req.getSkillCodes())); } catch (Exception e) {}
        }
        if (req.getVariables() != null) {
            try { app.setVariables(objectMapper.writeValueAsString(req.getVariables())); } catch (Exception e) {}
        }
        return app;
    }
}
