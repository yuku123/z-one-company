package com.zifang.z.meta.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.meta.core.entity.ZTenant;
import com.zifang.z.meta.core.mapper.ZTenantMapper;
import com.zifang.z.meta.core.service.ITenantService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 租户 Service 实现
 */
@Service
@Primary
public class TenantServiceImpl extends ServiceImpl<ZTenantMapper, ZTenant> implements ITenantService {

    @Override
    public IPage<ZTenant> pageTenant(Page<ZTenant> page, ZTenant tenant) {
        LambdaQueryWrapper<ZTenant> wrapper = new LambdaQueryWrapper<>();
        if (tenant != null) {
            if (StringUtils.hasText(tenant.getTenantCode())) {
                wrapper.like(ZTenant::getTenantCode, tenant.getTenantCode());
            }
            if (StringUtils.hasText(tenant.getTenantName())) {
                wrapper.like(ZTenant::getTenantName, tenant.getTenantName());
            }
            if (StringUtils.hasText(tenant.getTenantType())) {
                wrapper.eq(ZTenant::getTenantType, tenant.getTenantType());
            }
            if (tenant.getStatus() != null) {
                wrapper.eq(ZTenant::getStatus, tenant.getStatus());
            }
        }
        wrapper.orderByDesc(ZTenant::getGmtCreate);
        return page(page, wrapper);
    }

    @Override
    public ZTenant getByTenantCode(String tenantCode) {
        return getOne(new LambdaQueryWrapper<ZTenant>().eq(ZTenant::getTenantCode, tenantCode));
    }
}