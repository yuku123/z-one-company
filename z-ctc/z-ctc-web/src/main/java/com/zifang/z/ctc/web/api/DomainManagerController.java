package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.DomainBizService;
import com.zifang.ctc.core.service.dto.DomainDTO;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.DomainReq;
import com.zifang.z.ctc.web.api.response.DomainResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "域管理")
@RestController
@RequestMapping("/api/domain")
public class DomainManagerController {

    @Resource
    private DomainBizService domainBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public Result<List<DomainResp>> list() {
        List<DomainResp> data = domainBizService.list().stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<IPage<DomainResp>> page(@RequestBody DomainReq req) {
        return Result.success(domainBizService.pageByTenantCode(
                req.getTenantCode(), req.getPageNum().intValue(), req.getPageSize().intValue()
        ).convert(this::toResp));
    }

    @Operation(summary = "根据租户编码查询域列表")
    @GetMapping("/tenant/{tenantCode}")
    public Result<List<DomainResp>> listByTenantCode(@PathVariable String tenantCode) {
        List<DomainResp> data = domainBizService.listByTenantCode(tenantCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域编码查询")
    @GetMapping("/code/{domainCode}")
    public Result<DomainResp> getByDomainCode(@PathVariable String domainCode) {
        return Result.success(toResp(domainBizService.getByDomainCode(domainCode)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody DomainReq req) {
        DomainDTO dto = new DomainDTO();
        BeanUtils.copyProperties(req, dto);
        domainBizService.create(dto);
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody DomainReq req) {
        DomainDTO dto = new DomainDTO();
        BeanUtils.copyProperties(req, dto);
        domainBizService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{domainCode}/delete")
    public Result<Void> delete(@PathVariable String domainCode) {
        domainBizService.delete(domainCode);
        return Result.success();
    }

    private DomainResp toResp(DomainDTO dto) {
        if (dto == null) return null;
        DomainResp resp = new DomainResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }
}
