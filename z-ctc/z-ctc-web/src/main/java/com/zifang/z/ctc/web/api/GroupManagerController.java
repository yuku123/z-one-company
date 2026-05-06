package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.GroupBizService;
import com.zifang.ctc.core.service.dto.GroupDTO;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.GroupReq;
import com.zifang.z.ctc.web.api.response.GroupResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "用户组管理")
@RestController
@RequestMapping("/api/group")
public class GroupManagerController {

    @Resource
    private GroupBizService groupBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public Result<List<GroupResp>> list() {
        List<GroupResp> data = groupBizService.list().stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<IPage<GroupResp>> page(@RequestBody GroupReq req) {
        return Result.success(groupBizService.page(toDto(req)).convert(this::toResp));
    }

    @Operation(summary = "根据租户ID查询")
    @GetMapping("/tenant/{tenantId}")
    public Result<List<GroupResp>> listByTenantId(@PathVariable Long tenantId) {
        List<GroupResp> data = groupBizService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域ID查询")
    @GetMapping("/domain/{domainId}")
    public Result<List<GroupResp>> listByDomainId(@PathVariable Long domainId) {
        List<GroupResp> data = groupBizService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据组织ID查询")
    @GetMapping("/org/{orgId}")
    public Result<List<GroupResp>> listByOrgId(@PathVariable Long orgId) {
        List<GroupResp> data = groupBizService.listByOrgId(orgId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据部门ID查询")
    @GetMapping("/dept/{deptId}")
    public Result<List<GroupResp>> listByDeptId(@PathVariable Long deptId) {
        List<GroupResp> data = groupBizService.listByDeptId(deptId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/get")
    public Result<GroupResp> getById(@RequestParam Long id) {
        return Result.success(toResp(groupBizService.getById(id)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody GroupReq req) {
        groupBizService.add(toDto(req));
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody GroupReq req) {
        groupBizService.update(toDto(req));
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        groupBizService.delete(id);
        return Result.success();
    }

    private GroupResp toResp(GroupDTO dto) {
        if (dto == null) return null;
        GroupResp resp = new GroupResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private GroupDTO toDto(GroupReq req) {
        GroupDTO dto = new GroupDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
