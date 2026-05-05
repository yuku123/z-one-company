package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.service.PermissionBizService;
import com.zifang.ctc.core.service.RoleBizService;
import com.zifang.ctc.core.service.dto.PermissionDTO;
import com.zifang.ctc.core.service.dto.RoleDTO;
import com.zifang.ctc.core.service.model.request.RolePageReq;
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

    /**
     * 获取权限列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取权限列表")
    public List<PermissionResp> listPermissions() {
        return permissionBizService.list().stream()
                .map(this::toPermissionResp)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取权限")
    public PermissionResp getPermissionById(@PathVariable Long id) {
        return toPermissionResp(permissionBizService.getById(id));
    }

    /**
     * 创建权限
     */
    @PostMapping
    @Operation(summary = "创建权限")
    public void createPermission(@RequestBody PermissionReq req) {
        Permission permission = toPermissionEntity(req);
        permissionBizService.create(permission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public void updatePermission(@PathVariable Long id, @RequestBody PermissionReq req) {
        Permission permission = toPermissionEntity(req);
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
    public List<RoleResp> listRoles() {
        return roleBizService.list().stream()
                .map(this::toRoleResp)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询角色
     */
    @PostMapping("/role/page")
    @Operation(summary = "分页查询角色")
    public IPage<RoleResp> pageRoles(@RequestBody RolePageReq req) {
        IPage<RoleDTO> page = roleBizService.page(req);
        return page.convert(this::toRoleResp);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/role/{id}")
    @Operation(summary = "根据ID获取角色")
    public RoleResp getRoleById(@PathVariable Long id) {
        return toRoleResp(roleBizService.getById(id));
    }

    /**
     * 创建角色
     */
    @PostMapping("/role")
    @Operation(summary = "创建角色")
    public void createRole(@RequestBody RoleReq req) {
        Role role = toRoleEntity(req);
        roleBizService.create(role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/role/{id}")
    @Operation(summary = "更新角色")
    public void updateRole(@PathVariable Long id, @RequestBody RoleReq req) {
        Role role = toRoleEntity(req);
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
    public List<PermissionResp> getRolePermissions(@PathVariable Long roleId) {
        return roleBizService.getRolePermissions(roleId).stream()
                .map(this::toPermissionResp)
                .collect(Collectors.toList());
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
        BeanUtils.copyProperties(req, permission);
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
        private String icon;
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
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public Integer getSort() { return sort; }
        public void setSort(Integer sort) { this.sort = sort; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }
}
