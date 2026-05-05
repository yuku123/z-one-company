package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.DomainDO;
import com.zifang.ctc.core.domain.service.IDomainService;
import com.zifang.ctc.core.service.DomainBizService;
import com.zifang.ctc.core.service.dto.DomainDTO;
import com.zifang.ctc.core.service.dto.converter.DomainDtoConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainBizServiceImpl implements DomainBizService {

    @Resource
    private IDomainService domainService;

    @Override
    public List<DomainDTO> list() {
        return domainService.list().stream()
                .map(DomainDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public IPage<DomainDTO> page(DomainDTO dto) {
        LambdaQueryWrapper<DomainDO> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            if (dto.getDomainCode() != null && !dto.getDomainCode().isEmpty())
                wrapper.like(DomainDO::getDomainCode, dto.getDomainCode());
            if (dto.getDomainName() != null && !dto.getDomainName().isEmpty())
                wrapper.like(DomainDO::getDomainName, dto.getDomainName());
            if (dto.getTenantId() != null) wrapper.eq(DomainDO::getTenantId, dto.getTenantId());
            if (dto.getStatus() != null) wrapper.eq(DomainDO::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(DomainDO::getCreatedTime);
        Page<DomainDO> p = new Page<>(1, 10);
        return domainService.page(p, wrapper).convert(DomainDtoConverter::toDTO);
    }

    @Override
    public List<DomainDTO> listByTenantId(Long tenantId) {
        return domainService.listByTenantId(tenantId).stream()
                .map(DomainDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public DomainDTO getByDomainCode(String domainCode) {
        return DomainDtoConverter.toDTO(domainService.getByDomainCode(domainCode));
    }

    @Override
    public DomainDTO getById(Long id) {
        return DomainDtoConverter.toDTO(domainService.getById(id));
    }

    @Override
    public void add(DomainDTO dto) {
        DomainDO entity = new DomainDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        domainService.add(entity);
    }

    @Override
    public void update(DomainDTO dto) {
        DomainDO entity = new DomainDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        domainService.update(entity);
    }

    @Override
    public void delete(Long id) {
        domainService.delete(id);
    }
}
