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
        return Result.success(deptBizService.pageByTenantCode(
                req.getTenantCode(), req.getPageNum().intValue(), req.getPageSize().intValue()
        ).convert(this::toResp));
    }

    @Operation(summary = "根据租户编码查询部门列表")
    @GetMapping("/tenant/{tenantCode}")
    public Result<List<DeptResp>> listByTenantCode(@PathVariable String tenantCode) {
        List<DeptResp> data = deptBizService.listByTenantCode(tenantCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域编码查询部门列表")
    @GetMapping("/domain/{domainCode}")
    public Result<List<DeptResp>> listByDomainCode(@PathVariable String domainCode) {
        List<DeptResp> data = deptBizService.listByDomainCode(domainCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据组织编码查询部门列表")
    @GetMapping("/org/{orgCode}")
    public Result<List<DeptResp>> listByOrgCode(@PathVariable String orgCode) {
        List<DeptResp> data = deptBizService.listByOrgCode(orgCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据部门编码查询")
    @GetMapping("/code/{deptCode}")
    public Result<DeptResp> getByDeptCode(@PathVariable String deptCode) {
        return Result.success(toResp(deptBizService.getByDeptCode(deptCode)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody DeptReq req) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(req, dto);
        deptBizService.create(dto);
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody DeptReq req) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(req, dto);
        deptBizService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{deptCode}/delete")
    public Result<Void> delete(@PathVariable String deptCode) {
        deptBizService.delete(deptCode);
        return Result.success();
    }

    private DeptResp toResp(DeptDTO dto) {
        if (dto == null) return null;
        DeptResp resp = new DeptResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }
}
