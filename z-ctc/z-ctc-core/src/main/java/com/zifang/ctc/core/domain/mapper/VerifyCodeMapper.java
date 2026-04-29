package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.VerifyCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码Mapper
 */
@Mapper
public interface VerifyCodeMapper extends BaseMapper<VerifyCode> {
}