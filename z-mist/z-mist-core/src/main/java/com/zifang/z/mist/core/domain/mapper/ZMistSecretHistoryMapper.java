package com.zifang.z.mist.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.mist.core.domain.entity.ZMistSecretHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密钥历史 Mapper
 */
@Mapper
public interface ZMistSecretHistoryMapper extends BaseMapper<ZMistSecretHistory> {
}