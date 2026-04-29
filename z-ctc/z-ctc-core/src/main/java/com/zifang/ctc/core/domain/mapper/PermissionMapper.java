package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT * FROM sys_permission WHERE perm_code = #{permCode} AND status = 1")
    Permission selectByPermCode(@Param("permCode") String permCode);

    @Select("SELECT p.* FROM sys_permission p INNER JOIN sys_role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = #{roleId} AND p.status = 1")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT DISTINCT p.* FROM sys_permission p INNER JOIN sys_role_permission rp ON p.id = rp.permission_id INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id WHERE ur.user_id = #{userId} AND p.status = 1")
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_permission WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order")
    List<Permission> selectByParentId(@Param("parentId") Long parentId);
}
