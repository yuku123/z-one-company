package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.OrgDO;
import com.zifang.ctc.core.domain.service.IOrgService;
import com.zifang.ctc.core.service.OrgBizService;
import com.zifang.ctc.core.service.dto.OrgDTO;
import com.zifang.ctc.core.service.dto.converter.OrgDtoConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrgBizServiceImpl implements OrgBizService {

    @Resource
    private IOrgService orgService;

    @Override
    public List<OrgDTO> list() {
        return orgService.list().stream()
                .map(OrgDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public IPage<OrgDTO> page(OrgDTO dto) {
        LambdaQueryWrapper<OrgDO> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            if (dto.getId() != null) wrapper.eq(OrgDO::getId, dto.getId());
            if (dto.getOrgName() != null && !dto.getOrgName().isEmpty())
                wrapper.like(OrgDO::getOrgName, dto.getOrgName());
            if (dto.getTenantId() != null) wrapper.eq(OrgDO::getTenantId, dto.getTenantId());
            if (dto.getDomainId() != null) wrapper.eq(OrgDO::getDomainId, dto.getDomainId());
            if (dto.getStatus() != null) wrapper.eq(OrgDO::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(OrgDO::getCreatedTime);
        Page<OrgDO> p = new Page<>(1, 10);
        return orgService.page(p, wrapper).convert(OrgDtoConverter::toDTO);
    }

    @Override
    public List<OrgDTO> listByTenantId(Long tenantId) {
        return orgService.listByTenantId(tenantId).stream()
                .map(OrgDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrgDTO> listByDomainId(Long domainId) {
        return orgService.listByDomainId(domainId).stream()
                .map(OrgDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public OrgDTO getById(Long id) {
        return OrgDtoConverter.toDTO(orgService.getById(id));
    }

    @Override
    public void add(OrgDTO dto) {
        OrgDO entity = new OrgDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        orgService.add(entity);
    }

    @Override
    public void update(OrgDTO dto) {
        OrgDO entity = new OrgDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        orgService.update(entity);
    }

    @Override
    public void delete(Long id) {
        orgService.delete(id);
    }
}
