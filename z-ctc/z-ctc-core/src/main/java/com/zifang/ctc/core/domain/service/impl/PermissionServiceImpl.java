package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.mapper.PermissionMapper;
import com.zifang.ctc.core.domain.service.IPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Override
    public Permission selectByPermCode(String permCode) {
        return getOne(new LambdaQueryWrapper<Permission>().eq(Permission::getPermCode, permCode));
    }

    @Override
    public List<Permission> selectPermissionsByRoleId(Long roleId) {
        return list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1));
    }

    @Override
    public List<Permission> selectPermissionsByUserId(Long userId) {
        return list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1));
    }

    @Override
    public List<Permission> selectByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getParentId, parentId)
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder));
    }
}
