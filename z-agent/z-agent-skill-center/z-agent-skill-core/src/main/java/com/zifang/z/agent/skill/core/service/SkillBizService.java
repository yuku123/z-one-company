package com.zifang.z.agent.skill.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.z.agent.skill.core.service.dto.SkillDTO;
import com.zifang.z.agent.skill.core.service.dto.SkillVersionDTO;

import java.util.List;

public interface SkillBizService {

    IPage<SkillDTO> page(SkillDTO query, int pageNum, int pageSize);

    SkillDTO getBySkillCode(String skillCode);

    SkillDTO create(SkillDTO dto);

    SkillDTO update(SkillDTO dto);

    void delete(Long id);

    void publish(String skillCode);

    void install(String skillCode, String installedBy, String tenantCode);

    List<SkillDTO> hot(int limit);

    List<SkillVersionDTO> versions(String skillCode);

    SkillVersionDTO addVersion(SkillVersionDTO dto);

    java.util.Map<String, Object> stats();

}
