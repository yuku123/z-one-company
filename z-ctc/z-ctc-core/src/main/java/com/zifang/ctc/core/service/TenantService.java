package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.Tenant;

public interface TenantService extends IService<Tenant> {

    IPage<Tenant> page(Page<Tenant> page, Tenant tenant);

    Tenant getByTenantCode(String tenantCode);

    boolean add(Tenant tenant);

    boolean update(Tenant tenant);

    boolean delete(Long id);
}
