package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.Tenant;
import com.zifang.ctc.core.domain.service.ITenantService;
import com.zifang.ctc.core.service.TenantBizService;
import com.zifang.ctc.core.service.dto.TenantDTO;
import com.zifang.ctc.core.service.dto.converter.TenantDtoConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TenantBizServiceImpl implements TenantBizService {

    @Resource
    private ITenantService tenantService;

    @Override
    public IPage<TenantDTO> page(TenantDTO dto) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            if (dto.getTenantCode() != null && !dto.getTenantCode().isEmpty()) {
                wrapper.like(Tenant::getTenantCode, dto.getTenantCode());
            }
            if (dto.getTenantName() != null && !dto.getTenantName().isEmpty()) {
                wrapper.like(Tenant::getTenantName, dto.getTenantName());
            }
            if (dto.getStatus() != null) {
                wrapper.eq(Tenant::getStatus, dto.getStatus());
            }
        }
        wrapper.orderByDesc(Tenant::getGmtCreate);
        long pNum = dto.getPageNum() != null ? dto.getPageNum() : 1L;
        long pSize = dto.getPageSize() != null ? dto.getPageSize() : 20L;
        Page<Tenant> p = new Page<>(pNum, pSize);
        return tenantService.page(p, wrapper).convert(TenantDtoConverter::toDTO);
    }

    @Override
    public TenantDTO getByTenantCode(String tenantCode) {
        return TenantDtoConverter.toDTO(tenantService.getByTenantCode(tenantCode));
    }

    @Override
    public void add(TenantDTO dto) {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        tenantService.save(tenant);
    }

    @Override
    public void update(TenantDTO dto) {
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        tenantService.updateById(tenant);
    }

    @Override
    public void delete(Long id) {
        tenantService.delete(id);
    }

    @Override
    public java.util.List<TenantDTO> list() {
        return tenantService.list().stream().map(TenantDtoConverter::toDTO).collect(java.util.stream.Collectors.toList());
    }
}
