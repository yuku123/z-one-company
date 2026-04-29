package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.mapper.RoleMapper;
import com.zifang.ctc.core.domain.mapper.UserRoleMapper;
import com.zifang.ctc.core.domain.service.IRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public Role selectByRoleCode(String roleCode) {
        return getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .eq(Role::getStatus, 1));
    }

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectByUserId(userId)
                .stream()
                .map(ur -> ur.getRoleId())
                .collect(java.util.stream.Collectors.toList());
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return listByIds(roleIds);
    }

    @Override
    public long countByRoleCode(String roleCode) {
        return count(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, roleCode));
    }
}
