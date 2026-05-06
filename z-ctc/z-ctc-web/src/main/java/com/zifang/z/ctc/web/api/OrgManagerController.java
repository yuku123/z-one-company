package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.OrgBizService;
import com.zifang.ctc.core.service.dto.OrgDTO;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.OrgReq;
import com.zifang.z.ctc.web.api.response.OrgResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "组织管理")
@RestController
@RequestMapping("/api/org")
public class OrgManagerController {

    @Resource
    private OrgBizService orgBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public Result<List<OrgResp>> list() {
        List<OrgResp> data = orgBizService.list().stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<IPage<OrgResp>> page(@RequestBody OrgReq req) {
        return Result.success(orgBizService.page(toDto(req)).convert(this::toResp));
    }

    @Operation(summary = "根据租户ID查询")
    @GetMapping("/tenant/{tenantId}")
    public Result<List<OrgResp>> listByTenantId(@PathVariable Long tenantId) {
        List<OrgResp> data = orgBizService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域ID查询")
    @GetMapping("/domain/{domainId}")
    public Result<List<OrgResp>> listByDomainId(@PathVariable Long domainId) {
        List<OrgResp> data = orgBizService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/get")
    public Result<OrgResp> getById(@RequestParam Long id) {
        return Result.success(toResp(orgBizService.getById(id)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody OrgReq req) {
        orgBizService.add(toDto(req));
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody OrgReq req) {
        orgBizService.update(toDto(req));
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        orgBizService.delete(id);
        return Result.success();
    }

    private OrgResp toResp(OrgDTO dto) {
        if (dto == null) return null;
        OrgResp resp = new OrgResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private OrgDTO toDto(OrgReq req) {
        OrgDTO dto = new OrgDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
