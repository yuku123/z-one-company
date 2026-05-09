package com.zifang.z.agent.skill.core.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.skill.core.domain.entity.SkillVersion;
import com.zifang.z.agent.skill.core.domain.mapper.SkillVersionMapper;
import com.zifang.z.agent.skill.core.domain.service.ISkillVersionService;
import org.springframework.stereotype.Service;

@Service
public class SkillVersionServiceImpl extends ServiceImpl<SkillVersionMapper, SkillVersion> implements ISkillVersionService {
}
