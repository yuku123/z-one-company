package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.DeptBizService;
import com.zifang.ctc.core.service.dto.DeptDTO;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.DeptReq;
import com.zifang.z.ctc.web.api.response.DeptResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/dept")
public class DeptManagerController {

    @Resource
    private DeptBizService deptBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public Result<List<DeptResp>> list() {
        List<DeptResp> data = deptBizService.list().stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<IPage<DeptResp>> page(@RequestBody DeptReq req) {
        return Result.success(deptBizService.page(toDto(req)).convert(this::toResp));
    }

    @Operation(summary = "根据租户ID查询")
    @GetMapping("/tenant/{tenantId}")
    public Result<List<DeptResp>> listByTenantId(@PathVariable Long tenantId) {
        List<DeptResp> data = deptBizService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域ID查询")
    @GetMapping("/domain/{domainId}")
    public Result<List<DeptResp>> listByDomainId(@PathVariable Long domainId) {
        List<DeptResp> data = deptBizService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据组织ID查询")
    @GetMapping("/org/{orgId}")
    public Result<List<DeptResp>> listByOrgId(@PathVariable Long orgId) {
        List<DeptResp> data = deptBizService.listByOrgId(orgId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/get")
    public Result<DeptResp> getById(@RequestParam Long id) {
        return Result.success(toResp(deptBizService.getById(id)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody DeptReq req) {
        deptBizService.add(toDto(req));
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody DeptReq req) {
        deptBizService.update(toDto(req));
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        deptBizService.delete(id);
        return Result.success();
    }

    private DeptResp toResp(DeptDTO dto) {
        if (dto == null) return null;
        DeptResp resp = new DeptResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private DeptDTO toDto(DeptReq req) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
