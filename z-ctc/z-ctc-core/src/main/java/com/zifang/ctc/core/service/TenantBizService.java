package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.TenantDTO;

public interface TenantBizService {
    IPage<TenantDTO> page(TenantDTO tenant);
    TenantDTO getByTenantCode(String tenantCode);
    TenantDTO getById(Long id);
    void add(TenantDTO tenant);
    void update(TenantDTO tenant);
    void delete(Long id);
    java.util.List<TenantDTO> list();
}
