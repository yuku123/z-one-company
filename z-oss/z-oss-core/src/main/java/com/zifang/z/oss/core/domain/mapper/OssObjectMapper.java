package com.zifang.z.oss.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.oss.core.domain.entity.OssObject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对象Mapper
 */
@Mapper
public interface OssObjectMapper extends BaseMapper<OssObject> {
}