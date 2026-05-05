package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.DomainDO;

import java.util.List;

public interface IDomainService extends IService<DomainDO> {

    IPage<DomainDO> page(Page<DomainDO> page, DomainDO domainDO);

    List<DomainDO> listByTenantId(Long tenantId);

    DomainDO getByDomainCode(String domainCode);

    boolean add(DomainDO domainDO);

    boolean update(DomainDO domainDO);

    boolean delete(Long id);
}
