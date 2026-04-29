package com.zifang.z.meta.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.meta.core.entity.ZTenant;

/**
 * 租户 Service
 */
public interface ITenantService extends IService<ZTenant> {

    /**
     * 分页查询租户
     */
    IPage<ZTenant> pageTenant(Page<ZTenant> page, ZTenant tenant);

    /**
     * 根据租户编码查询
     */
    ZTenant getByTenantCode(String tenantCode);
}