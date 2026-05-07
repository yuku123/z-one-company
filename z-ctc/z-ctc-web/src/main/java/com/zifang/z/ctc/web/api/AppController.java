package com.zifang.z.ctc.web.api;

import com.zifang.ctc.core.domain.entity.AppDO;
import com.zifang.ctc.core.domain.entity.AppMenu;
import com.zifang.ctc.core.domain.service.IAppMenuService;
import com.zifang.ctc.core.domain.service.IAppService;
import com.zifang.util.core.meta.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppController {

    @Resource private IAppService appService;
    @Resource private IAppMenuService appMenuService;

    // ===== App CRUD =====
    @GetMapping("/list")
    public Result<List<AppDO>> list(@RequestParam(required = false) String tenantCode,
                                    @RequestParam(required = false) String domainCode) {
        if (tenantCode != null) return Result.success(appService.listByTenant(tenantCode));
        if (domainCode != null) return Result.success(appService.listByDomain(domainCode));
        return Result.success(appService.list());
    }

    @PostMapping
    public Result<Void> create(@RequestBody AppDO app) {
        app.setGmtCreate(LocalDateTime.now());
        appService.save(app);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody AppDO app) {
        appService.updateById(app);
        return Result.success();
    }

    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        appService.removeById(id);
        return Result.success();
    }

    // ===== Menu CRUD =====
    @GetMapping("/menu/list")
    public Result<List<AppMenu>> menuList(@RequestParam String appCode) {
        return Result.success(appMenuService.listByApp(appCode));
    }

    @PostMapping("/menu")
    public Result<Void> createMenu(@RequestBody AppMenu menu) {
        menu.setGmtCreate(LocalDateTime.now());
        appMenuService.save(menu);
        return Result.success();
    }

    @PostMapping("/menu/update")
    public Result<Void> updateMenu(@RequestBody AppMenu menu) {
        appMenuService.updateById(menu);
        return Result.success();
    }

    @PostMapping("/menu/{id}/delete")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        appMenuService.removeById(id);
        return Result.success();
    }
}
