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
        return Result.success(orgBizService.pageByTenantCode(
                req.getTenantCode(), req.getPageNum(), req.getPageSize()
        ).convert(this::toResp));
    }

    @Operation(summary = "根据租户编码查询组织列表")
    @GetMapping("/tenant/{tenantCode}")
    public Result<List<OrgResp>> listByTenantCode(@PathVariable String tenantCode) {
        List<OrgResp> data = orgBizService.listByTenantCode(tenantCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域编码查询组织列表")
    @GetMapping("/domain/{domainCode}")
    public Result<List<OrgResp>> listByDomainCode(@PathVariable String domainCode) {
        List<OrgResp> data = orgBizService.listByDomainCode(domainCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据组织编码查询")
    @GetMapping("/code/{orgCode}")
    public Result<OrgResp> getByOrgCode(@PathVariable String orgCode) {
        return Result.success(toResp(orgBizService.getByOrgCode(orgCode)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody OrgReq req) {
        OrgDTO dto = new OrgDTO();
        BeanUtils.copyProperties(req, dto);
        orgBizService.create(dto);
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody OrgReq req) {
        OrgDTO dto = new OrgDTO();
        BeanUtils.copyProperties(req, dto);
        orgBizService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{orgCode}/delete")
    public Result<Void> delete(@PathVariable String orgCode) {
        orgBizService.delete(orgCode);
        return Result.success();
    }

    private OrgResp toResp(OrgDTO dto) {
        if (dto == null) return null;
        OrgResp resp = new OrgResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }
}
