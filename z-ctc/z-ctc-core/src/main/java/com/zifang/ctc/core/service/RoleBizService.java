package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.service.model.request.RolePageReq;

import java.util.List;

/**
 * 角色业务服务接口
 */
public interface RoleBizService {

    /**
     * 获取角色列表
     */
    List<Role> list();

    /**
     * 分页查询角色
     * @param req 分页请求（含 current/size 和搜索字段）
     * @return 分页结果
     */
    IPage<Role> page(RolePageReq req);

    /**
     * 根据ID获取角色
     */
    Role getById(Long id);

    /**
     * 创建角色
     */
    void create(Role role);

    /**
     * 更新角色
     */
    void update(Role role);

    /**
     * 删除角色
     */
    void delete(Long id);

    /**
     * 为角色分配权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色的权限列表
     */
    List<Permission> getRolePermissions(Long roleId);
}
