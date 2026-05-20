package com.zifang.z.agent.center.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.agent.share.dto.AgentShareDto;
import com.zifang.z.agent.center.core.agent.share.service.AgentShareService;
import com.zifang.z.agent.center.core.agent.share.dto.AgentShareReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "Agent分享")
@RestController
@RequestMapping("/api/agent/share")
public class AgentShareController {

    @Resource
    private AgentShareService shareService;

    @Operation(summary = "生成分享链接")
    @PostMapping("/create")
    public Result<AgentShareDto> create(@RequestBody AgentShareReq req) {
        return Result.success(shareService.createResp(req));
    }

    @Operation(summary = "验证分享码并返回实例信息")
    @GetMapping("/verify")
    public Result<AgentShareDto> verify(@RequestParam String shareCode) {
        return Result.success(shareService.verifyResp(shareCode));
    }

    @Operation(summary = "查询实例的所有分享记录")
    @GetMapping("/list")
    public Result<List<AgentShareDto>> list(@RequestParam String instanceCode) {
        return Result.success(shareService.listRespByInstance(instanceCode));
    }

    @Operation(summary = "禁用分享")
    @PostMapping("/disable")
    public Result<Void> disable(@RequestParam String shareCode) {
        shareService.disable(shareCode);
        return Result.success();
    }
}
