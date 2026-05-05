package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.OrgDO;

import java.util.List;

public interface IOrgService extends IService<OrgDO> {

    IPage<OrgDO> page(Page<OrgDO> page, OrgDO orgDO);

    List<OrgDO> listByTenantId(Long tenantId);

    List<OrgDO> listByDomainId(Long domainId);

    OrgDO getByOrgId(Long orgId);

    boolean add(OrgDO orgDO);

    boolean update(OrgDO orgDO);

    boolean delete(Long id);
}
