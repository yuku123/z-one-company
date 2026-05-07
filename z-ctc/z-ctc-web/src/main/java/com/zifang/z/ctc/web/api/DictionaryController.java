package com.zifang.z.ctc.web.api;

import com.zifang.ctc.core.domain.entity.Dictionary;
import com.zifang.ctc.core.domain.service.IDictionaryService;
import com.zifang.util.core.meta.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dict")
public class DictionaryController {

    @Resource
    private IDictionaryService dictionaryService;

    // 按租户查所有
    @GetMapping("/list")
    public Result<List<Dictionary>> list(@RequestParam String tenantCode) {
        return Result.success(dictionaryService.listByTenant(tenantCode));
    }

    // 查询 category 列表
    @GetMapping("/categories")
    public Result<List<String>> categories(@RequestParam String tenantCode) {
        return Result.success(dictionaryService.listCategories(tenantCode));
    }

    // 初始化
    @PostMapping("/init")
    public Result<String> init(@RequestBody Map<String, String> params) {
        dictionaryService.initBuiltin(params.get("tenantCode"), params.get("domainCode"));
        return Result.success();
    }

    // 是否已初始化
    @GetMapping("/has-init")
    public Result<Boolean> hasInit(@RequestParam String tenantCode) {
        return Result.success(dictionaryService.hasInit(tenantCode));
    }

    // 新增
    @PostMapping
    public Result<Void> add(@RequestBody Dictionary dict) {
        dict.setIsBuiltin(0);
        dict.setGmtCreate(java.time.LocalDateTime.now());
        dictionaryService.save(dict);
        return Result.success();
    }

    // 更新
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Dictionary dict) {
        dictionaryService.updateById(dict);
        return Result.success();
    }

    // 删除
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        dictionaryService.removeById(id);
        return Result.success();
    }

    // 批量更新排序
    @PostMapping("/reorder")
    public Result<Void> reorder(@RequestBody List<Dictionary> list) {
        for (Dictionary d : list) {
            dictionaryService.lambdaUpdate()
                .eq(Dictionary::getId, d.getId()).set(Dictionary::getSortOrder, d.getSortOrder()).update();
        }
        return Result.success();
    }
}
