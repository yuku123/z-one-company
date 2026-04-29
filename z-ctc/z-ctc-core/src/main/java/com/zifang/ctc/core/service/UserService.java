package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.domain.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUserName(String userName);

    /**
     * 根据用户名查询用户（包含密码）
     */
    User getByUserNameWithPassword(String userName);

    /**
     * 根据租户编码查询用户列表
     */
    List<User> getByTenantCode(String tenantCode);

    /**
     * 查询用户的角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 查询用户的权限列表
     */
    List<Permission> getUserPermissions(Long userId);

    /**
     * 创建用户
     */
    boolean createUser(User user);

    /**
     * 更新用户
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long userId);

    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 分配用户角色
     */
    boolean assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 更新登录信息
     */
    boolean updateLoginInfo(Long userId, String ip);
}
