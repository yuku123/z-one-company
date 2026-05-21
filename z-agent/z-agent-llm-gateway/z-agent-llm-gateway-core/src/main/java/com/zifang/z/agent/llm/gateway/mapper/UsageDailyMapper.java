package com.zifang.z.agent.llm.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.agent.llm.gateway.entity.UsageDaily;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsageDailyMapper extends BaseMapper<UsageDaily> {
}
