package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.DomainDTO;
import java.util.List;

public interface DomainBizService {
    IPage<DomainDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize);
    List<DomainDTO> listByTenantCode(String tenantCode);
    DomainDTO getByDomainCode(String domainCode);
    List<DomainDTO> list();
    boolean delete(String domainCode);
    boolean create(DomainDTO dto);
    boolean update(DomainDTO dto);
}
