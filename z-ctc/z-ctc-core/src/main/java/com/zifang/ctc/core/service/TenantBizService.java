package com.zifang.ctc.core.service;

import com.zifang.ctc.core.domain.entity.Tenant;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TenantBizService {

    IPage<Tenant> page(Page<Tenant> page, Tenant tenant);

    Tenant getByTenantCode(String tenantCode);

    boolean add(Tenant tenant);

    boolean update(Tenant tenant);

    boolean delete(Long id);
}
