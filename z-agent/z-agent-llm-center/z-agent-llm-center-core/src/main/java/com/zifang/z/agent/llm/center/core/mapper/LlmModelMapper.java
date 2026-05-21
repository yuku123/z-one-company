package com.zifang.z.agent.llm.center.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.agent.llm.center.core.entity.LlmModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LlmModelMapper extends BaseMapper<LlmModel> {
}
