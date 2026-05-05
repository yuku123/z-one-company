package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.entity.RolePermission;
import com.zifang.ctc.core.domain.entity.UserRole;
import com.zifang.ctc.core.domain.mapper.PermissionMapper;
import com.zifang.ctc.core.domain.mapper.RoleMapper;
import com.zifang.ctc.core.domain.mapper.RolePermissionMapper;
import com.zifang.ctc.core.domain.mapper.UserRoleMapper;
import com.zifang.ctc.core.domain.service.IRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public Role selectByRoleCode(String roleCode) {
        return getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .eq(Role::getStatus, 1));
    }

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        return listByIds(roleIds);
    }

    @Override
    public long countByRoleCode(String roleCode) {
        return count(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, roleCode));
    }

    /**
     * 为角色分配权限
     */
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        for (Long permissionId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rolePermissionMapper.insert(rp);
        }
    }
}
