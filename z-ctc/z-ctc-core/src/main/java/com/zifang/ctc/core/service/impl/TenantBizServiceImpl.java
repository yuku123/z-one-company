package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.Tenant;
import com.zifang.ctc.core.domain.mapper.TenantMapper;
import com.zifang.ctc.core.service.TenantBizService;
import com.zifang.ctc.core.service.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class TenantBizServiceImpl implements TenantBizService {

    @Resource
    private TenantService tenantService;

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
        return tenantService.page(page, wrapper);
    }

    @Override
    public Tenant getByTenantCode(String tenantCode) {
        return tenantService.getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getTenantCode, tenantCode));
    }

    @Override
    public boolean add(Tenant tenant) {
        tenant.setGmtCreate(LocalDateTime.now());
        tenant.setGmtModified(LocalDateTime.now());
        return tenantService.save(tenant);
    }

    @Override
    public boolean update(Tenant tenant) {
        tenant.setGmtModified(LocalDateTime.now());
        return tenantService.updateById(tenant);
    }

    @Override
    public boolean delete(Long id) {
        return tenantService.removeById(id);
    }
}
