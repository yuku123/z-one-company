package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.domain.entity.UserRole;
import com.zifang.ctc.core.domain.mapper.PermissionMapper;
import com.zifang.ctc.core.domain.mapper.RoleMapper;
import com.zifang.ctc.core.domain.mapper.UserMapper;
import com.zifang.ctc.core.domain.mapper.UserRoleMapper;
import com.zifang.ctc.core.domain.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public User selectByUserName(String userName) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
    }

    @Override
    public User selectByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }

    @Override
    public User selectByEmail(String email) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    @Override
    public long countByUserName(String userName) {
        return count(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
    }

    @Override
    public IPage<User> selectPage(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUserName, keyword)
                   .or()
                   .like(User::getPhone, keyword)
                   .or()
                   .like(User::getEmail, keyword);
        }
        return page(page, wrapper);
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        return roleMapper.selectBatchIds(roleIds);
    }

    @Override
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        for (Long roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }
}
