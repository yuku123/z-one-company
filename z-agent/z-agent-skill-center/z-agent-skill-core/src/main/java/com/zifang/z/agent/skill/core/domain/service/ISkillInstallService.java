package com.zifang.z.agent.skill.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.agent.skill.core.domain.entity.SkillInstall;

import java.util.List;

public interface ISkillInstallService extends IService<SkillInstall> {

    List<SkillInstall> listBySkillCode(String skillCode);

}
