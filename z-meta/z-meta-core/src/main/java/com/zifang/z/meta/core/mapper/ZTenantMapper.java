package com.zifang.z.meta.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.meta.core.entity.ZTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper
 */
@Mapper
public interface ZTenantMapper extends BaseMapper<ZTenant> {
}