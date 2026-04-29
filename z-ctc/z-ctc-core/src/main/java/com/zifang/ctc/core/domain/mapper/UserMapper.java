package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

//    @Select("SELECT * FROM sys_user WHERE user_name = #{userName} AND status = 1")
//    User selectByUserName(@Param("userName") String userName);
//
//    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND status = 1")
//    User selectByPhone(@Param("phone") String phone);
//
//    @Select("SELECT COUNT(*) FROM sys_user WHERE user_name = #{userName}")
//    int countByUserName(@Param("userName") String userName);
//
//    @Select("SELECT * FROM sys_user WHERE email = #{email} AND status = 1")
//    User selectByEmail(@Param("email") String email);
}
