package com.zifang.z.agent.llm.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.agent.llm.gateway.entity.UsageRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsageRecordMapper extends BaseMapper<UsageRecord> {
}
