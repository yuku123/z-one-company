package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID查询关联
     */
    List<RolePermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询关联
     */
    List<RolePermission> selectByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据角色ID删除关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID删除关联
     */
    int deleteByPermissionId(@Param("permissionId") Long permissionId);
}
