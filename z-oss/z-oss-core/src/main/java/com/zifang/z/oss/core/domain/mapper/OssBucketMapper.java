package com.zifang.z.oss.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.oss.core.domain.entity.OssBucket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 桶Mapper
 */
@Mapper
public interface OssBucketMapper extends BaseMapper<OssBucket> {
}