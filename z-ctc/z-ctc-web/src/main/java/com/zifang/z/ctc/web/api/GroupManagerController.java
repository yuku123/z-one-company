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
        return Result.success(groupBizService.pageByTenantCode(
                req.getTenantCode(), req.getPageNum(), req.getPageSize()
        ).convert(this::toResp));
    }

    @Operation(summary = "根据租户编码查询用户组列表")
    @GetMapping("/tenant/{tenantCode}")
    public Result<List<GroupResp>> listByTenantCode(@PathVariable String tenantCode) {
        List<GroupResp> data = groupBizService.listByTenantCode(tenantCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据域编码查询用户组列表")
    @GetMapping("/domain/{domainCode}")
    public Result<List<GroupResp>> listByDomainCode(@PathVariable String domainCode) {
        List<GroupResp> data = groupBizService.listByDomainCode(domainCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据组织编码查询用户组列表")
    @GetMapping("/org/{orgCode}")
    public Result<List<GroupResp>> listByOrgCode(@PathVariable String orgCode) {
        List<GroupResp> data = groupBizService.listByOrgCode(orgCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据部门编码查询用户组列表")
    @GetMapping("/dept/{deptCode}")
    public Result<List<GroupResp>> listByDeptCode(@PathVariable String deptCode) {
        List<GroupResp> data = groupBizService.listByDeptCode(deptCode).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "根据组编码查询")
    @GetMapping("/code/{groupCode}")
    public Result<GroupResp> getByGroupCode(@PathVariable String groupCode) {
        return Result.success(toResp(groupBizService.getByGroupCode(groupCode)));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody GroupReq req) {
        GroupDTO dto = new GroupDTO();
        BeanUtils.copyProperties(req, dto);
        groupBizService.create(dto);
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody GroupReq req) {
        GroupDTO dto = new GroupDTO();
        BeanUtils.copyProperties(req, dto);
        groupBizService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/{groupCode}/delete")
    public Result<Void> delete(@PathVariable String groupCode) {
        groupBizService.delete(groupCode);
        return Result.success();
    }

    private GroupResp toResp(GroupDTO dto) {
        if (dto == null) return null;
        GroupResp resp = new GroupResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }
}
