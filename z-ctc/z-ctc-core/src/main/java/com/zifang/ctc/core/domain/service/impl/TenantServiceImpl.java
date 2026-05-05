package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.Tenant;
import com.zifang.ctc.core.domain.mapper.TenantMapper;
import com.zifang.ctc.core.domain.service.ITenantService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements ITenantService {

    @Override
    public IPage<Tenant> page(Page<Tenant> page, Tenant tenant) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (tenant != null) {
            if (StringUtils.hasText(tenant.getTenantCode())) {
                wrapper.like(Tenant::getTenantCode, tenant.getTenantCode());
            }
            if (StringUtils.hasText(tenant.getTenantName())) {
                wrapper.like(Tenant::getTenantName, tenant.getTenantName());
            }
            if (tenant.getStatus() != null) {
                wrapper.eq(Tenant::getStatus, tenant.getStatus());
            }
        }
        wrapper.orderByDesc(Tenant::getGmtCreate);
        return page(page, wrapper);
    }

    @Override
    public Tenant getByTenantCode(String tenantCode) {
        return getOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantCode, tenantCode));
    }

    @Override
    public boolean add(Tenant tenant) {
        tenant.setGmtCreate(LocalDateTime.now());
        tenant.setGmtModified(LocalDateTime.now());
        return save(tenant);
    }

    @Override
    public boolean update(Tenant tenant) {
        tenant.setGmtModified(LocalDateTime.now());
        return updateById(tenant);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
