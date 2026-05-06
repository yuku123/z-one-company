package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.TenantDTO;
import java.util.List;

public interface TenantBizService {
    IPage<TenantDTO> page(TenantDTO tenant);
    TenantDTO getByTenantCode(String tenantCode);
    void add(TenantDTO tenant);
    void update(TenantDTO tenant);
    void delete(Long id);
    List<TenantDTO> list();
}
