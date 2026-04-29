package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.DeptDO;
import com.zifang.ctc.core.service.DeptService;
import com.zifang.z.ctc.web.api.request.DeptReq;
import com.zifang.z.ctc.web.api.response.DeptResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dept")
@Tag(name = "004_部门管理")
public class DeptManagerController {

    @Resource
    private DeptService deptService;

    @GetMapping("/list")
    @Operation(summary = "列表")
    public List<DeptResp> list() {
        return deptService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页列表")
    public IPage<DeptResp> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            DeptReq req) {
        DeptDO entity = toEntity(req);
        IPage<DeptDO> page = deptService.page(new Page<>(current, pageSize), entity);
        return page.convert(this::toResp);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "根据租户ID查询部门列表")
    public List<DeptResp> listByTenantId(@PathVariable Long tenantId) {
        return deptService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "根据域ID查询部门列表")
    public List<DeptResp> listByDomainId(@PathVariable Long domainId) {
        return deptService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/org/{orgId}")
    @Operation(summary = "根据组织ID查询部门列表")
    public List<DeptResp> listByOrgId(@PathVariable Long orgId) {
        return deptService.listByOrgId(orgId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "详情")
    public DeptResp getById(@PathVariable Long id) {
        return toResp(deptService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增")
    public boolean add(@RequestBody DeptReq req) {
        return deptService.add(toEntity(req));
    }

    @PutMapping
    @Operation(summary = "更新")
    public boolean update(@RequestBody DeptReq req) {
        return deptService.update(toEntity(req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public boolean delete(@PathVariable Long id) {
        return deptService.delete(id);
    }

    private DeptResp toResp(DeptDO entity) {
        if (entity == null) return null;
        DeptResp resp = new DeptResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }

    private DeptDO toEntity(DeptReq req) {
        if (req == null) return null;
        DeptDO entity = new DeptDO();
        BeanUtils.copyProperties(req, entity);
        return entity;
    }
}
