package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.entity.User;

import java.util.List;

public interface IUserService extends IService<User> {

    User selectByUserName(String userName);

    User selectByPhone(String phone);

    User selectByEmail(String email);

    long countByUserName(String userName);

    IPage<User> selectPage(Page<User> page, String keyword);

    /**
     * 获取用户的角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 分配用户角色
     */
    void assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的权限列表
     */
    List<Permission> getUserPermissions(Long userId);
}
