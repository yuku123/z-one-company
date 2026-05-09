package com.zifang.z.agent.skill.core.service;

import com.zifang.z.agent.skill.core.service.dto.SkillCategoryDTO;

import java.util.List;

public interface SkillCategoryBizService {

    List<SkillCategoryDTO> tree();

    SkillCategoryDTO create(SkillCategoryDTO dto);

    void delete(Long id);

}
