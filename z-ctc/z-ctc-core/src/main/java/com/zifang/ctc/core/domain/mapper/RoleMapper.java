package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND status = 1")
    Role selectByRoleCode(@Param("roleCode") String roleCode);

    @Select("SELECT COUNT(*) FROM sys_role WHERE role_code = #{roleCode}")
    int countByRoleCode(@Param("roleCode") String roleCode);
}
