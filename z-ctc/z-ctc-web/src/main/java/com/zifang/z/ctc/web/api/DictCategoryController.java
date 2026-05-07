package com.zifang.z.ctc.web.api;

import com.zifang.ctc.core.domain.entity.DictCategory;
import com.zifang.ctc.core.domain.service.IDictCategoryService;
import com.zifang.util.core.meta.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dict-category")
public class DictCategoryController {

    @Resource
    private IDictCategoryService categoryService;

    @GetMapping("/list")
    public Result<List<DictCategory>> list(@RequestParam String tenantCode) {
        return Result.success(categoryService.listByTenant(tenantCode));
    }

    @PostMapping
    public Result<Void> create(@RequestBody DictCategory cat) {
        cat.setGmtCreate(LocalDateTime.now());
        categoryService.save(cat);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody DictCategory cat) {
        categoryService.updateById(cat);
        return Result.success();
    }

    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
