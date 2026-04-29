package com.zifang.z.mist.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.mist.core.domain.entity.ZMistSecretAcl;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密钥 ACL Mapper
 */
@Mapper
public interface ZMistSecretAclMapper extends BaseMapper<ZMistSecretAcl> {
}