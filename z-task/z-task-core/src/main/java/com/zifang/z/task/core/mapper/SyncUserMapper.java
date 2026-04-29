package com.zifang.z.task.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.SyncUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户同步表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface SyncUserMapper extends BaseMapper<SyncUser> {
}
