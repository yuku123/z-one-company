package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.DeptDO;
import java.util.List;

public interface IDeptService extends IService<DeptDO> {
    IPage<DeptDO> pageByTenantCode(Page<DeptDO> page, String tenantCode);
    List<DeptDO> listByTenantCode(String tenantCode);
    List<DeptDO> listByDomainCode(String domainCode);
    List<DeptDO> listByOrgCode(String orgCode);
    DeptDO getByDeptCode(String deptCode);
    Long getIdByDeptCode(String deptCode);
    boolean deleteByDeptCode(String deptCode);
}
