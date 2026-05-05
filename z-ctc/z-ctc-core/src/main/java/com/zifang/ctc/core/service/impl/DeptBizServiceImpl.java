package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.DeptDO;
import com.zifang.ctc.core.domain.service.IDeptService;
import com.zifang.ctc.core.service.DeptBizService;
import com.zifang.ctc.core.service.dto.DeptDTO;
import com.zifang.ctc.core.service.dto.converter.DeptDtoConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptBizServiceImpl implements DeptBizService {

    @Resource
    private IDeptService deptService;

    @Override
    public List<DeptDTO> list() {
        return deptService.list().stream()
                .map(DeptDtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<DeptDTO> page(DeptDTO dto) {
        LambdaQueryWrapper<DeptDO> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            if (dto.getId() != null) wrapper.eq(DeptDO::getId, dto.getId());
            if (dto.getDeptName() != null && !dto.getDeptName().isEmpty())
                wrapper.like(DeptDO::getDeptName, dto.getDeptName());
            if (dto.getTenantId() != null) wrapper.eq(DeptDO::getTenantId, dto.getTenantId());
            if (dto.getDomainId() != null) wrapper.eq(DeptDO::getDomainId, dto.getDomainId());
            if (dto.getOrgId() != null) wrapper.eq(DeptDO::getOrgId, dto.getOrgId());
            if (dto.getStatus() != null) wrapper.eq(DeptDO::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(DeptDO::getCreatedTime);
        Page<DeptDO> p = new Page<>(1, 10);
        return deptService.page(p, wrapper).convert(DeptDtoConverter::toDTO);
    }

    @Override
    public List<DeptDTO> listByTenantId(Long tenantId) {
        return deptService.listByTenantId(tenantId).stream()
                .map(DeptDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DeptDTO> listByDomainId(Long domainId) {
        return deptService.listByDomainId(domainId).stream()
                .map(DeptDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DeptDTO> listByOrgId(Long orgId) {
        return deptService.listByOrgId(orgId).stream()
                .map(DeptDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public DeptDTO getById(Long id) {
        return DeptDtoConverter.toDTO(deptService.getById(id));
    }

    @Override
    public void add(DeptDTO dto) {
        DeptDO entity = new DeptDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        deptService.add(entity);
    }

    @Override
    public void update(DeptDTO dto) {
        DeptDO entity = new DeptDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        deptService.update(entity);
    }

    @Override
    public void delete(Long id) {
        deptService.delete(id);
    }
}
