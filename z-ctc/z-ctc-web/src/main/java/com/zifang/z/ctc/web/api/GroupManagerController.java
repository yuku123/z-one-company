package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.GroupDO;
import com.zifang.ctc.core.service.GroupService;
import com.zifang.z.ctc.web.api.request.GroupReq;
import com.zifang.z.ctc.web.api.response.GroupResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group")
@Tag(name = "005_组管理")
public class GroupManagerController {

    @Resource
    private GroupService groupService;

    @GetMapping("/list")
    @Operation(summary = "列表")
    public List<GroupResp> list() {
        return groupService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页列表")
    public IPage<GroupResp> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            GroupReq req) {
        GroupDO entity = toEntity(req);
        IPage<GroupDO> page = groupService.page(new Page<>(current, pageSize), entity);
        return page.convert(this::toResp);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "根据租户ID查询组列表")
    public List<GroupResp> listByTenantId(@PathVariable Long tenantId) {
        return groupService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "根据域ID查询组列表")
    public List<GroupResp> listByDomainId(@PathVariable Long domainId) {
        return groupService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/org/{orgId}")
    @Operation(summary = "根据组织ID查询组列表")
    public List<GroupResp> listByOrgId(@PathVariable Long orgId) {
        return groupService.listByOrgId(orgId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据部门ID查询组列表")
    public List<GroupResp> listByDeptId(@PathVariable Long deptId) {
        return groupService.listByDeptId(deptId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "详情")
    public GroupResp getById(@PathVariable Long id) {
        return toResp(groupService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增")
    public boolean add(@RequestBody GroupReq req) {
        return groupService.add(toEntity(req));
    }

    @PutMapping
    @Operation(summary = "更新")
    public boolean update(@RequestBody GroupReq req) {
        return groupService.update(toEntity(req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public boolean delete(@PathVariable Long id) {
        return groupService.delete(id);
    }

    private GroupResp toResp(GroupDO entity) {
        if (entity == null) return null;
        GroupResp resp = new GroupResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }

    private GroupDO toEntity(GroupReq req) {
        if (req == null) return null;
        GroupDO entity = new GroupDO();
        BeanUtils.copyProperties(req, entity);
        return entity;
    }
}
