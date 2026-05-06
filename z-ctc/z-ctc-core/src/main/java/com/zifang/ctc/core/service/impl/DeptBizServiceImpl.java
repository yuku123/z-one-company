package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.DeptDO;
import com.zifang.ctc.core.domain.service.IDeptService;
import com.zifang.ctc.core.service.DeptBizService;
import com.zifang.ctc.core.service.dto.DeptDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptBizServiceImpl implements DeptBizService {
    private final IDeptService deptService;

    public DeptBizServiceImpl(IDeptService deptService) {
        this.deptService = deptService;
    }

    private DeptDTO toDTO(DeptDO d) {
        if (d == null) return null;
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(d, dto);
        return dto;
    }

    @Override
    public IPage<DeptDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize) {
        Page<DeptDO> page = new Page<>(pageNum, pageSize);
        IPage<DeptDO> result = deptService.pageByTenantCode(page, tenantCode);
        return result.convert(this::toDTO);
    }

    @Override
    public List<DeptDTO> listByTenantCode(String tenantCode) {
        return deptService.listByTenantCode(tenantCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DeptDTO> listByDomainCode(String domainCode) {
        return deptService.listByDomainCode(domainCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DeptDTO> listByOrgCode(String orgCode) {
        return deptService.listByOrgCode(orgCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DeptDTO getByDeptCode(String deptCode) {
        return toDTO(deptService.getByDeptCode(deptCode));
    }

    @Override
    public List<DeptDTO> list() {
        return deptService.list().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String deptCode) {
        return deptService.deleteByDeptCode(deptCode);
    }

    @Override
    public boolean create(DeptDTO dto) {
        DeptDO d = new DeptDO();
        BeanUtils.copyProperties(dto, d);
        return deptService.save(d);
    }

    @Override
    public boolean update(DeptDTO dto) {
        DeptDO d = new DeptDO();
        BeanUtils.copyProperties(dto, d);
        return deptService.updateById(d);
    }
}
