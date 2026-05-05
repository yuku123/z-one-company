package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.OrgDTO;

import java.util.List;

public interface OrgBizService {
    List<OrgDTO> list();
    IPage<OrgDTO> page(OrgDTO org);
    List<OrgDTO> listByTenantId(Long tenantId);
    List<OrgDTO> listByDomainId(Long domainId);
    OrgDTO getById(Long id);
    void add(OrgDTO org);
    void update(OrgDTO org);
    void delete(Long id);
}
