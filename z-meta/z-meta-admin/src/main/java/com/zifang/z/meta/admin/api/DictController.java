package com.zifang.z.meta.admin.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.z.meta.common.model.Result;
import com.zifang.z.meta.core.entity.ZDictType;
import com.zifang.z.meta.core.entity.ZDictItem;
import com.zifang.z.meta.core.service.IDictService;
import com.zifang.z.meta.core.service.IDictItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理 Controller
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private IDictService dictService;

    @Autowired
    private IDictItemService dictItemService;

    // ==================== 字典类型 ====================

    @Operation(summary = "分页查询字典类型")
    @GetMapping("/type/list")
    public Result<IPage<ZDictType>> listDictType(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long pageSize,
            @Parameter(description = "租户ID") @RequestParam(required = false) Long tenantId,
            @Parameter(description = "字典编码") @RequestParam(required = false) String dictCode,
            @Parameter(description = "字典名称") @RequestParam(required = false) String dictName,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        ZDictType dictType = new ZDictType();
        dictType.setTenantId(tenantId);
        if (StringUtils.hasText(dictCode)) {
            dictType.setDictCode(dictCode);
        }
        if (StringUtils.hasText(dictName)) {
            dictType.setDictName(dictName);
        }
        dictType.setStatus(status);

        Page<ZDictType> page = new Page<>(pageNum, pageSize);
        IPage<ZDictType> result = dictService.pageDictType(page, dictType);
        return Result.success(result);
    }

    @Operation(summary = "获取字典类型详情")
    @GetMapping("/type/{id}")
    public Result<ZDictType> getDictTypeById(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        ZDictType dictType = dictService.getById(id);
        return Result.success(dictType);
    }

    @Operation(summary = "创建字典类型")
    @PostMapping("/type")
    public Result<ZDictType> createDictType(@RequestBody ZDictType dictType) {
        // 检查字典编码是否已存在
        ZDictType exist = dictService.getByDictCode(dictType.getTenantId(), dictType.getDictCode());
        if (exist != null) {
            return Result.error("字典编码已存在");
        }
        dictService.save(dictType);
        return Result.success(dictType);
    }

    @Operation(summary = "更新字典类型")
    @PutMapping("/type/{id}")
    public Result<ZDictType> updateDictType(@Parameter(description = "字典类型ID") @PathVariable Long id, @RequestBody ZDictType dictType) {
        dictType.setId(id);
        dictService.updateById(dictType);
        return Result.success(dictService.getById(id));
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{id}")
    public Result<Void> deleteDictType(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        dictService.removeById(id);
        return Result.success();
    }

    // ==================== 字典项 ====================

    @Operation(summary = "根据字典ID获取字典项列表")
    @GetMapping("/item/{dictId}")
    public Result<List<ZDictItem>> listDictItem(@Parameter(description = "字典类型ID") @PathVariable Long dictId) {
        List<ZDictItem> items = dictService.listByDictId(dictId);
        return Result.success(items);
    }

    @Operation(summary = "根据字典编码获取字典项列表")
    @GetMapping("/items/{dictCode}")
    public Result<List<ZDictItem>> listDictItemByCode(@Parameter(description = "字典编码") @PathVariable String dictCode) {
        List<ZDictItem> items = dictService.listByDictCode(dictCode);
        return Result.success(items);
    }

    @Operation(summary = "创建字典项")
    @PostMapping("/item")
    public Result<ZDictItem> createDictItem(@RequestBody ZDictItem dictItem) {
        dictItemService.save(dictItem);
        return Result.success(dictItem);
    }

    @Operation(summary = "更新字典项")
    @PutMapping("/item/{id}")
    public Result<ZDictItem> updateDictItem(@Parameter(description = "字典项ID") @PathVariable Long id, @RequestBody ZDictItem dictItem) {
        dictItem.setId(id);
        dictItemService.updateById(dictItem);
        return Result.success(dictItem);
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/item/{id}")
    public Result<Void> deleteDictItem(@Parameter(description = "字典项ID") @PathVariable Long id) {
        dictItemService.removeById(id);
        return Result.success();
    }
}