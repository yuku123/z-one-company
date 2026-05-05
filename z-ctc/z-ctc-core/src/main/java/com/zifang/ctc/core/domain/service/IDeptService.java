package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.DeptDO;

import java.util.List;

public interface IDeptService extends IService<DeptDO> {

    IPage<DeptDO> page(Page<DeptDO> page, DeptDO deptDO);

    List<DeptDO> listByTenantId(Long tenantId);

    List<DeptDO> listByDomainId(Long domainId);

    List<DeptDO> listByOrgId(Long orgId);

    DeptDO getByDeptId(Long deptId);

    boolean add(DeptDO deptDO);

    boolean update(DeptDO deptDO);

    boolean delete(Long id);
}
