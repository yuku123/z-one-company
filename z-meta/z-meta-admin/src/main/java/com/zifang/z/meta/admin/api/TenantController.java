package com.zifang.z.meta.admin.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.z.meta.common.model.Result;
import com.zifang.z.meta.core.entity.ZTenant;
import com.zifang.z.meta.core.service.ITenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理 Controller
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    @Qualifier("tenantServiceImpl")
    private ITenantService tenantService;

    @Operation(summary = "分页查询租户")
    @GetMapping("/list")
    public Result<IPage<ZTenant>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long pageSize,
            @Parameter(description = "租户编码") @RequestParam(required = false) String tenantCode,
            @Parameter(description = "租户名称") @RequestParam(required = false) String tenantName,
            @Parameter(description = "租户类型") @RequestParam(required = false) String tenantType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        ZTenant tenant = new ZTenant();
        if (StringUtils.hasText(tenantCode)) {
            tenant.setTenantCode(tenantCode);
        }
        if (StringUtils.hasText(tenantName)) {
            tenant.setTenantName(tenantName);
        }
        if (StringUtils.hasText(tenantType)) {
            tenant.setTenantType(tenantType);
        }
        tenant.setStatus(status);

        Page<ZTenant> page = new Page<>(pageNum, pageSize);
        IPage<ZTenant> result = tenantService.pageTenant(page, tenant);
        return Result.success(result);
    }

    @Operation(summary = "获取租户详情")
    @GetMapping("/{id}")
    public Result<ZTenant> getById(@Parameter(description = "租户ID") @PathVariable Long id) {
        ZTenant tenant = tenantService.getById(id);
        return Result.success(tenant);
    }

    @Operation(summary = "创建租户")
    @PostMapping
    public Result<ZTenant> create(@RequestBody ZTenant tenant) {
        // 检查租户编码是否已存在
        ZTenant exist = tenantService.getByTenantCode(tenant.getTenantCode());
        if (exist != null) {
            return Result.error("租户编码已存在");
        }
        tenantService.save(tenant);
        return Result.success(tenant);
    }

    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    public Result<ZTenant> update(@Parameter(description = "租户ID") @PathVariable Long id, @RequestBody ZTenant tenant) {
        tenant.setId(id);
        tenantService.updateById(tenant);
        return Result.success(tenantService.getById(id));
    }

    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "租户ID") @PathVariable Long id) {
        tenantService.removeById(id);
        return Result.success();
    }
}