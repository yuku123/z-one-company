package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.DomainDO;
import com.zifang.ctc.core.domain.service.IDomainService;
import com.zifang.ctc.core.service.DomainBizService;
import com.zifang.ctc.core.service.dto.DomainDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainBizServiceImpl implements DomainBizService {
    private final IDomainService domainService;

    public DomainBizServiceImpl(IDomainService domainService) {
        this.domainService = domainService;
    }

    private DomainDTO toDTO(DomainDO d) {
        if (d == null) return null;
        DomainDTO dto = new DomainDTO();
        BeanUtils.copyProperties(d, dto);
        return dto;
    }

    @Override
    public IPage<DomainDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize) {
        Page<DomainDO> page = new Page<>(pageNum, pageSize);
        IPage<DomainDO> result = domainService.pageByTenantCode(page, tenantCode);
        return result.convert(this::toDTO);
    }

    @Override
    public List<DomainDTO> listByTenantCode(String tenantCode) {
        return domainService.listByTenantCode(tenantCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DomainDTO getByDomainCode(String domainCode) {
        return toDTO(domainService.getByDomainCode(domainCode));
    }

    @Override
    public List<DomainDTO> list() {
        return domainService.list().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String domainCode) {
        return domainService.deleteByDomainCode(domainCode);
    }

    @Override
    public boolean create(DomainDTO dto) {
        DomainDO d = new DomainDO();
        BeanUtils.copyProperties(dto, d);
        return domainService.save(d);
    }

    @Override
    public boolean update(DomainDTO dto) {
        DomainDO d = new DomainDO();
        BeanUtils.copyProperties(dto, d);
        return domainService.updateById(d);
    }
}
