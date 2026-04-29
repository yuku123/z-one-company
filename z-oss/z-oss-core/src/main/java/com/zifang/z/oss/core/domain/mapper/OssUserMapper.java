package com.zifang.z.oss.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.oss.core.domain.entity.OssUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface OssUserMapper extends BaseMapper<OssUser> {
}