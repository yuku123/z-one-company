package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.OrgDO;
import java.util.List;

public interface IOrgService extends IService<OrgDO> {
    IPage<OrgDO> pageByTenantCode(Page<OrgDO> page, String tenantCode);
    List<OrgDO> listByTenantCode(String tenantCode);
    List<OrgDO> listByDomainCode(String domainCode);
    OrgDO getByOrgCode(String orgCode);
    Long getIdByOrgCode(String orgCode);
    boolean deleteByOrgCode(String orgCode);
}
