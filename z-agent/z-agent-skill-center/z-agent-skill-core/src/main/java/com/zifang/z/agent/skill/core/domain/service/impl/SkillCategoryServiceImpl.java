package com.zifang.z.agent.skill.core.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.skill.core.domain.entity.SkillCategory;
import com.zifang.z.agent.skill.core.domain.mapper.SkillCategoryMapper;
import com.zifang.z.agent.skill.core.domain.service.ISkillCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillCategoryServiceImpl extends ServiceImpl<SkillCategoryMapper, SkillCategory> implements ISkillCategoryService {

    @Override
    public List<SkillCategory> listAll() {
        return this.baseMapper.selectList(null);
    }

}
