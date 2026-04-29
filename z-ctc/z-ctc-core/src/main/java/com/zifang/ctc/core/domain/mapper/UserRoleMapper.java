package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("SELECT * FROM z_ctc_user_role WHERE user_id = #{userId}")
    List<UserRole> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM z_ctc_user_role WHERE role_id = #{roleId}")
    List<UserRole> selectByRoleId(@Param("roleId") Long roleId);

    @Delete("DELETE FROM z_ctc_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM z_ctc_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
}
