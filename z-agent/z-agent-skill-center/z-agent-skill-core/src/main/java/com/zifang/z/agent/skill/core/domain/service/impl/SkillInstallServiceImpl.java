package com.zifang.z.agent.skill.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.skill.core.domain.entity.SkillInstall;
import com.zifang.z.agent.skill.core.domain.mapper.SkillInstallMapper;
import com.zifang.z.agent.skill.core.domain.service.ISkillInstallService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillInstallServiceImpl extends ServiceImpl<SkillInstallMapper, SkillInstall> implements ISkillInstallService {

    @Override
    public List<SkillInstall> listBySkillCode(String skillCode) {
        LambdaQueryWrapper<SkillInstall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillInstall::getSkillCode, skillCode);
        return this.baseMapper.selectList(wrapper);
    }

}
