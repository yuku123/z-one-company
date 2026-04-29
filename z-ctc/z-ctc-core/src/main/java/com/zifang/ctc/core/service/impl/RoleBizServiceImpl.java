package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.service.IPermissionService;
import com.zifang.ctc.core.domain.service.IRoleService;
import com.zifang.ctc.core.service.RoleBizService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleBizServiceImpl implements RoleBizService {

    @Resource
    private IRoleService roleService;

    @Resource
    private IPermissionService permissionService;

    @Override
    public List<Role> list() {
        return roleService.list(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getStatus, 1)
                        .orderByDesc(Role::getGmtCreate)
        );
    }

    @Override
    public Role getById(Long id) {
        return roleService.getById(id);
    }

    @Override
    @Transactional
    public void create(Role role) {
        if (roleService.selectByRoleCode(role.getRoleCode()) != null) {
            throw new RuntimeException("角色编码已存在");
        }

        role.setStatus(1);
        role.setGmtCreate(LocalDateTime.now());
        role.setGmtModified(LocalDateTime.now());

        roleService.save(role);
    }

    @Override
    @Transactional
    public void update(Role role) {
        Role existingRole = roleService.getById(role.getId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }

        role.setRoleCode(null);
        role.setStatus(null);
        role.setGmtModified(LocalDateTime.now());

        roleService.updateById(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(0);
        role.setGmtModified(LocalDateTime.now());
        roleService.updateById(role);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionService.selectPermissionsByRoleId(roleId);
    }
}
