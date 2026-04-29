package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.Permission;

import java.util.List;

public interface IPermissionService extends IService<Permission> {

    Permission selectByPermCode(String permCode);

    List<Permission> selectPermissionsByRoleId(Long roleId);

    List<Permission> selectPermissionsByUserId(Long userId);

    List<Permission> selectByParentId(Long parentId);
}
