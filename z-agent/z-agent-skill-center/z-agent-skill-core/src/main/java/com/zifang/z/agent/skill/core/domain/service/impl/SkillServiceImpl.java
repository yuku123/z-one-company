package com.zifang.z.agent.skill.core.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.skill.core.domain.entity.Skill;
import com.zifang.z.agent.skill.core.domain.mapper.SkillMapper;
import com.zifang.z.agent.skill.core.domain.service.ISkillService;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements ISkillService {
}
