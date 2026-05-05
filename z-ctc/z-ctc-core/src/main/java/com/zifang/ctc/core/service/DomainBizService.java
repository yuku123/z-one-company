package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.DomainDTO;

import java.util.List;

public interface DomainBizService {
    List<DomainDTO> list();
    IPage<DomainDTO> page(DomainDTO domain);
    List<DomainDTO> listByTenantId(Long tenantId);
    DomainDTO getByDomainCode(String domainCode);
    DomainDTO getById(Long id);
    void add(DomainDTO domain);
    void update(DomainDTO domain);
    void delete(Long id);
}
