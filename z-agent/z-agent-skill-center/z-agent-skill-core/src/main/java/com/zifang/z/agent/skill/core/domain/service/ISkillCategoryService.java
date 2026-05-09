package com.zifang.z.agent.skill.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.agent.skill.core.domain.entity.SkillCategory;

import java.util.List;

public interface ISkillCategoryService extends IService<SkillCategory> {

    List<SkillCategory> listAll();

}
