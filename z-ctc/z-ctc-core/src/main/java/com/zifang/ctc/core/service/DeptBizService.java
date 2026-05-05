package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.DeptDTO;

import java.util.List;

public interface DeptBizService {
    List<DeptDTO> list();
    IPage<DeptDTO> page(DeptDTO dept);
    List<DeptDTO> listByTenantId(Long tenantId);
    List<DeptDTO> listByDomainId(Long domainId);
    List<DeptDTO> listByOrgId(Long orgId);
    DeptDTO getById(Long id);
    void add(DeptDTO dept);
    void update(DeptDTO dept);
    void delete(Long id);
}
