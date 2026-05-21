package com.zifang.z.meta.admin.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.z.meta.common.model.Result;
import com.zifang.z.meta.core.entity.ZApplication;
import com.zifang.z.meta.core.service.IApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 应用管理 Controller
 */
@Tag(name = "应用管理")
@RestController
@RequestMapping("/app")
public class ApplicationController {

    @Autowired
    private IApplicationService applicationService;

    @Operation(summary = "分页查询应用")
    @GetMapping("/list")
    public Result<IPage<ZApplication>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long pageSize,
            @Parameter(description = "租户ID") @RequestParam(required = false) Long tenantId,
            @Parameter(description = "应用编码") @RequestParam(required = false) String appCode,
            @Parameter(description = "应用名称") @RequestParam(required = false) String appName,
            @Parameter(description = "应用类型") @RequestParam(required = false) String appType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        ZApplication application = new ZApplication();
        application.setTenantId(tenantId);
        if (StringUtils.hasText(appCode)) {
            application.setAppCode(appCode);
        }
        if (StringUtils.hasText(appName)) {
            application.setAppName(appName);
        }
        if (StringUtils.hasText(appType)) {
            application.setAppType(appType);
        }
        application.setStatus(status);

        Page<ZApplication> page = new Page<>(pageNum, pageSize);
        IPage<ZApplication> result = applicationService.pageApplication(page, application);
        return Result.success(result);
    }

    @Operation(summary = "获取应用详情")
    @GetMapping("/get")
    public Result<ZApplication> getById(@Parameter(description = "应用ID") @RequestParam Long id) {
        ZApplication application = applicationService.getById(id);
        return Result.success(application);
    }

    @Operation(summary = "创建应用")
    @PostMapping
    public Result<ZApplication> create(@RequestBody ZApplication application) {
        // 检查应用编码是否已存在
        ZApplication exist = applicationService.getByAppCode(application.getTenantId(), application.getAppCode());
        if (exist != null) {
            return Result.error("应用编码已存在");
        }
        applicationService.save(application);
        return Result.success(application);
    }

    @Operation(summary = "更新应用")
    @PutMapping("/{id}")
    public Result<ZApplication> update(@Parameter(description = "应用ID") @PathVariable Long id, @RequestBody ZApplication application) {
        application.setId(id);
        applicationService.updateById(application);
        return Result.success(applicationService.getById(id));
    }

    @Operation(summary = "删除应用")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "应用ID") @PathVariable Long id) {
        applicationService.removeById(id);
        return Result.success();
    }
}