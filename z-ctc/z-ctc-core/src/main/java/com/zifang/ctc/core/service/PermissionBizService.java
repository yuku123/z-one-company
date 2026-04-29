package com.zifang.ctc.core.service;

import com.zifang.ctc.core.domain.entity.Permission;

import java.util.List;

/**
 * 权限业务服务接口
 */
public interface PermissionBizService {

    /**
     * 获取权限列表
     */
    List<Permission> list();

    /**
     * 根据ID获取权限
     */
    Permission getById(Long id);

    /**
     * 创建权限
     */
    void create(Permission permission);

    /**
     * 更新权限
     */
    void update(Permission permission);

    /**
     * 删除权限
     */
    void delete(Long id);

    /**
     * 根据父ID获取子权限
     */
    List<Permission> getByParentId(Long parentId);

    /**
     * 根据用户ID获取权限列表
     */
    List<Permission> getUserPermissions(Long userId);

    /**
     * 根据角色ID获取权限列表
     */
    List<Permission> getRolePermissions(Long roleId);
}
