package com.zifang.z.agent.center.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.agent.share.entity.AgentShare;
import com.zifang.z.agent.center.core.agent.share.service.AgentShareService;
import com.zifang.z.agent.center.web.api.request.AgentShareReq;
import com.zifang.z.agent.center.web.api.response.AgentShareResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Agent分享")
@RestController
@RequestMapping("/api/agent/share")
public class AgentShareController {

    @Resource
    private AgentShareService shareService;

    @Operation(summary = "生成分享链接")
    @PostMapping("/create")
    public Result<AgentShareResp> create(@RequestBody AgentShareReq req) {
        AgentShare share = shareService.createShare(req.getInstanceCode(), req.getAppCode());
        return Result.success(toResp(share));
    }

    @Operation(summary = "验证分享码并返回实例信息")
    @GetMapping("/verify")
    public Result<AgentShareResp> verify(@RequestParam String shareCode) {
        AgentShare share = shareService.getByShareCode(shareCode);
        if (share != null) {
            shareService.incrementVisitCount(shareCode);
        }
        return Result.success(toResp(share));
    }

    @Operation(summary = "查询实例的所有分享记录")
    @GetMapping("/list")
    public Result<List<AgentShareResp>> list(@RequestParam String instanceCode) {
        List<AgentShare> list = shareService.listByInstance(instanceCode);
        return Result.success(list.stream().map(this::toResp).collect(Collectors.toList()));
    }

    @Operation(summary = "禁用分享")
    @PostMapping("/disable")
    public Result<Void> disable(@RequestParam String shareCode) {
        shareService.disable(shareCode);
        return Result.success();
    }

    private AgentShareResp toResp(AgentShare share) {
        if (share == null) return null;
        AgentShareResp resp = new AgentShareResp();
        BeanUtils.copyProperties(share, resp);
        resp.setShareUrl("/share/" + share.getShareCode());
        return resp;
    }
}
