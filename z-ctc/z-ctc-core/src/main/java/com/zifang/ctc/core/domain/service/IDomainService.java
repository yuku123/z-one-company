package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.DomainDO;
import java.util.List;

public interface IDomainService extends IService<DomainDO> {
    IPage<DomainDO> pageByTenantCode(Page<DomainDO> page, String tenantCode);
    List<DomainDO> listByTenantCode(String tenantCode);
    DomainDO getByDomainCode(String domainCode);
    Long getIdByDomainCode(String domainCode);
    boolean deleteByDomainCode(String domainCode);
}
