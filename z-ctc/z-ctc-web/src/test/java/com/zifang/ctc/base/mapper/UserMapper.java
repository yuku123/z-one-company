package com.zifang.ctc.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.base.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author miemie
 * @since 2018-08-10
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
