package com.zifang.z.meta.admin.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.z.meta.common.model.Result;
import com.zifang.z.meta.core.entity.ZApi;
import com.zifang.z.meta.core.service.IApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口管理 Controller
 */
@Tag(name = "接口管理")
@RestController
@RequestMapping("/interface")
public class ApiController {

    @Autowired
    private IApiService apiService;

    @Operation(summary = "分页查询接口")
    @GetMapping("/list")
    public Result<IPage<ZApi>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long pageSize,
            @Parameter(description = "应用ID") @RequestParam(required = false) Long appId,
            @Parameter(description = "接口路径") @RequestParam(required = false) String apiPath,
            @Parameter(description = "接口名称") @RequestParam(required = false) String apiName,
            @Parameter(description = "请求方法") @RequestParam(required = false) String apiMethod,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "是否废弃") @RequestParam(required = false) Integer deprecated) {

        ZApi api = new ZApi();
        api.setAppId(appId);
        if (StringUtils.hasText(apiPath)) {
            api.setApiPath(apiPath);
        }
        if (StringUtils.hasText(apiName)) {
            api.setApiName(apiName);
        }
        if (StringUtils.hasText(apiMethod)) {
            api.setApiMethod(apiMethod);
        }
        api.setStatus(status);
        api.setDeprecated(deprecated);

        Page<ZApi> page = new Page<>(pageNum, pageSize);
        IPage<ZApi> result = apiService.pageApi(page, api);
        return Result.success(result);
    }

    @Operation(summary = "获取接口详情")
    @GetMapping("/{id}")
    public Result<ZApi> getById(@Parameter(description = "接口ID") @PathVariable Long id) {
        ZApi api = apiService.getById(id);
        return Result.success(api);
    }

    @Operation(summary = "根据应用ID获取接口列表")
    @GetMapping("/app/{appId}")
    public Result<List<ZApi>> listByAppId(@Parameter(description = "应用ID") @PathVariable Long appId) {
        List<ZApi> apis = apiService.listByAppId(appId);
        return Result.success(apis);
    }

    @Operation(summary = "创建接口")
    @PostMapping
    public Result<ZApi> create(@RequestBody ZApi api) {
        apiService.save(api);
        return Result.success(api);
    }

    @Operation(summary = "更新接口")
    @PutMapping("/{id}")
    public Result<ZApi> update(@Parameter(description = "接口ID") @PathVariable Long id, @RequestBody ZApi api) {
        api.setId(id);
        apiService.updateById(api);
        return Result.success(apiService.getById(id));
    }

    @Operation(summary = "删除接口")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "接口ID") @PathVariable Long id) {
        apiService.removeById(id);
        return Result.success();
    }
}