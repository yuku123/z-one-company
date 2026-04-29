package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.Tenant;
import com.zifang.ctc.core.service.TenantService;
import com.zifang.z.ctc.web.api.request.TenantReq;
import com.zifang.z.ctc.web.api.response.TenantResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tenant")
@Tag(name = "001_租户管理")
public class TenantManagerController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/list")
    @Operation(summary = "列表")
    public List<TenantResp> list() {
        return tenantService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页列表")
    public IPage<TenantResp> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            TenantReq req) {
        Tenant tenant = toEntity(req);
        IPage<Tenant> page = tenantService.page(new Page<>(current, pageSize), tenant);
        return page.convert(this::toResp);
    }

    @GetMapping("/code/{tenantCode}")
    @Operation(summary = "根据租户编码查询")
    public TenantResp getByTenantCode(@PathVariable String tenantCode) {
        return toResp(tenantService.getByTenantCode(tenantCode));
    }

    @GetMapping("/{id}")
    @Operation(summary = "详情")
    public TenantResp getById(@PathVariable Long id) {
        return toResp(tenantService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增")
    public boolean add(@RequestBody TenantReq req) {
        Tenant tenant = toEntity(req);
        return tenantService.add(tenant);
    }

    @PutMapping
    @Operation(summary = "更新")
    public boolean update(@RequestBody TenantReq req) {
        Tenant tenant = toEntity(req);
        return tenantService.update(tenant);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public boolean delete(@PathVariable Long id) {
        return tenantService.delete(id);
    }

    private TenantResp toResp(Tenant tenant) {
        if (tenant == null) return null;
        TenantResp resp = new TenantResp();
        BeanUtils.copyProperties(tenant, resp);
        return resp;
    }

    private Tenant toEntity(TenantReq req) {
        if (req == null) return null;
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(req, tenant);
        return tenant;
    }
}
