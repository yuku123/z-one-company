package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.service.PermissionBizService;
import com.zifang.ctc.core.service.RoleBizService;
import com.zifang.ctc.core.service.dto.PermissionDTO;
import com.zifang.ctc.core.service.dto.RoleDTO;
import com.zifang.ctc.core.service.model.request.RolePageReq;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.RoleReq;
import com.zifang.z.ctc.web.api.response.PermissionResp;
import com.zifang.z.ctc.web.api.response.RoleResp;
import org.springframework.beans.BeanUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理控制器 - 4A授权模块
 */
@RestController
@RequestMapping("/api/permission")
@Tag(name = "权限管理")
public class PermissionManagerController {

    @Resource
    private PermissionBizService permissionBizService;

    @Resource
    private RoleBizService roleBizService;

    @GetMapping("/list")
    @Operation(summary = "获取权限列表")
    public Result<List<PermissionResp>> listPermissions() {
        List<PermissionResp> data = permissionBizService.list().stream()
                .map(this::toPermissionResp)
                .collect(Collectors.toList());
        return Result.success(data);
    }

    @GetMapping("/get")
    @Operation(summary = "根据ID获取权限")
    public Result<PermissionResp> getPermissionById(@RequestParam Long id) {
        return Result.success(toPermissionResp(permissionBizService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "创建权限")
    public Result<Void> createPermission(@RequestBody PermissionReq req) {
        Permission permission = toPermissionEntity(req);
        permissionBizService.create(permission);
        return Result.success();
    }

    @PostMapping("/{id}/update")
    @Operation(summary = "更新权限")
    public Result<Void> updatePermission(@PathVariable Long id, @RequestBody PermissionReq req) {
        Permission permission = toPermissionEntity(req);
        permission.setId(id);
        permissionBizService.update(permission);
        return Result.success();
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "删除权限")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionBizService.delete(id);
        return Result.success();
    }

    // ==================== 角色管理 ====================

    @GetMapping("/role/list")
    @Operation(summary = "获取角色列表")
    public Result<List<RoleResp>> listRoles() {
        List<RoleResp> data = roleBizService.list().stream()
                .map(this::toRoleResp)
                .collect(Collectors.toList());
        return Result.success(data);
    }

    @PostMapping("/role/page")
    @Operation(summary = "分页查询角色")
    public Result<IPage<RoleResp>> pageRoles(@RequestBody RolePageReq req) {
        return Result.success(roleBizService.page(req).convert(this::toRoleResp));
    }

    @GetMapping("/role/get")
    @Operation(summary = "根据ID获取角色")
    public Result<RoleResp> getRoleById(@RequestParam Long id) {
        return Result.success(toRoleResp(roleBizService.getById(id)));
    }

    @PostMapping("/role")
    @Operation(summary = "创建角色")
    public Result<Void> createRole(@RequestBody RoleReq req) {
        Role role = toRoleEntity(req);
        roleBizService.create(role);
        return Result.success();
    }

    @PostMapping("/role/{id}/update")
    @Operation(summary = "更新角色")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody RoleReq req) {
        Role role = toRoleEntity(req);
        role.setId(id);
        roleBizService.update(role);
        return Result.success();
    }

    @PostMapping("/role/{id}/delete")
    @Operation(summary = "删除角色")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleBizService.delete(id);
        return Result.success();
    }

    @PostMapping("/role/{roleId}/permissions")
    @Operation(summary = "为角色分配权限")
    public Result<Void> assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleBizService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }

    @GetMapping("/role/permissions")
    @Operation(summary = "获取角色的权限列表")
    public Result<List<PermissionResp>> getRolePermissions(@RequestParam Long roleId) {
        List<PermissionResp> data = roleBizService.getRolePermissions(roleId).stream()
                .map(this::toPermissionResp)
                .collect(Collectors.toList());
        return Result.success(data);
    }

    // ========== private convert methods ==========

    private RoleResp toRoleResp(RoleDTO dto) {
        if (dto == null) return null;
        RoleResp resp = new RoleResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private Role toRoleEntity(RoleReq req) {
        if (req == null) return null;
        Role role = new Role();
        BeanUtils.copyProperties(req, role);
        return role;
    }

    private PermissionResp toPermissionResp(PermissionDTO dto) {
        if (dto == null) return null;
        PermissionResp resp = new PermissionResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private Permission toPermissionEntity(PermissionReq req) {
        if (req == null) return null;
        Permission permission = new Permission();
        permission.setPermName(req.getPermissionName());
        permission.setPermCode(req.getPermissionCode());
        permission.setPermType(req.getResourceType()); // 字段名不同
        permission.setParentId(req.getParentId());
        permission.setPath(req.getPath());
        permission.setSortOrder(req.getSort()); // 字段名不同
        permission.setStatus(req.getStatus());
        return permission;
    }

    // inner request class for permission
    public static class PermissionReq {
        private Long id;
        private String permissionName;
        private String permissionCode;
        private String resourceType;
        private Long parentId;
        private String path;
        private Integer sort;
        private Integer status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getPermissionName() { return permissionName; }
        public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
        public String getPermissionCode() { return permissionCode; }
        public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public Integer getSort() { return sort; }
        public void setSort(Integer sort) { this.sort = sort; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }
}
