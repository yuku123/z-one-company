package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {

    Role selectByRoleCode(String roleCode);

    List<Role> selectRolesByUserId(Long userId);

    long countByRoleCode(String roleCode);
}
