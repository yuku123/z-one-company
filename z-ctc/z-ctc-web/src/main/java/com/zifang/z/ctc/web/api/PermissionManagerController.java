package com.zifang.z.ctc.web.api;


import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.service.PermissionBizService;
import com.zifang.ctc.core.service.RoleBizService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限管理控制器 - 4A授权模块
 */
@RestController
@RequestMapping("/permission")
@Tag(name = "权限管理")

public class PermissionManagerController {

    @Resource
    private PermissionBizService permissionBizService;

    @Resource
    private RoleBizService roleBizService;

    /**
     * 获取权限列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取权限列表")
    public List<Permission> listPermissions() {
        return permissionBizService.list();
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取权限")
    public Permission getPermissionById(@PathVariable Long id) {
        return permissionBizService.getById(id);
    }

    /**
     * 创建权限
     */
    @PostMapping
    @Operation(summary = "创建权限")
    public void createPermission(@RequestBody Permission permission) {
        permissionBizService.create(permission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public void updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionBizService.update(permission);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public void deletePermission(@PathVariable Long id) {
        permissionBizService.delete(id);
    }

    // ==================== 角色管理 ====================

    /**
     * 获取角色列表
     */
    @GetMapping("/role/list")
    @Operation(summary = "获取角色列表")
    public List<Role> listRoles() {
        return roleBizService.list();
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/role/{id}")
    @Operation(summary = "根据ID获取角色")
    
    public Role getRoleById(@PathVariable Long id) {
        return roleBizService.getById(id);
    }

    /**
     * 创建角色
     */
    @PostMapping("/role")
    @Operation(summary = "创建角色")
    public void createRole(@RequestBody Role role) {
        roleBizService.create(role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/role/{id}")
    @Operation(summary = "更新角色")
    public void updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleBizService.update(role);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/role/{id}")
    @Operation(summary = "删除角色")
    public void deleteRole(@PathVariable Long id) {
        roleBizService.delete(id);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/role/{roleId}/permissions")
    @Operation(summary = "为角色分配权限")
    public void assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleBizService.assignPermissions(roleId, permissionIds);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/role/{roleId}/permissions")
    @Operation(summary = "获取角色的权限列表")
    public List<Permission> getRolePermissions(@PathVariable Long roleId) {
        return roleBizService.getRolePermissions(roleId);
    }
}