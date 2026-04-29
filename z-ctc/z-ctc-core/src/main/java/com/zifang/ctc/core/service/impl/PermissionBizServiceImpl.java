package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.service.IPermissionService;
import com.zifang.ctc.core.service.PermissionBizService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionBizServiceImpl implements PermissionBizService {

    @Resource
    private IPermissionService permissionService;

    @Override
    public List<Permission> list() {
        return permissionService.list(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, 1)
                        .orderByAsc(Permission::getSortOrder)
        );
    }

    @Override
    public Permission getById(Long id) {
        return permissionService.getById(id);
    }

    @Override
    @Transactional
    public void create(Permission permission) {
        if (permissionService.selectByPermCode(permission.getPermCode()) != null) {
            throw new RuntimeException("权限编码已存在");
        }

        permission.setStatus(1);
        permission.setGmtCreate(LocalDateTime.now());
        permission.setGmtModified(LocalDateTime.now());

        permissionService.save(permission);
    }

    @Override
    @Transactional
    public void update(Permission permission) {
        Permission existingPermission = permissionService.getById(permission.getId());
        if (existingPermission == null) {
            throw new RuntimeException("权限不存在");
        }

        permission.setPermCode(null);
        permission.setStatus(null);
        permission.setGmtModified(LocalDateTime.now());

        permissionService.updateById(permission);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setStatus(0);
        permission.setGmtModified(LocalDateTime.now());
        permissionService.updateById(permission);
    }

    @Override
    public List<Permission> getByParentId(Long parentId) {
        return permissionService.selectByParentId(parentId);
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionService.selectPermissionsByUserId(userId);
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionService.selectPermissionsByRoleId(roleId);
    }
}
