package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.OrgDO;
import com.zifang.ctc.core.domain.service.IOrgService;
import com.zifang.ctc.core.service.OrgBizService;
import com.zifang.ctc.core.service.dto.OrgDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrgBizServiceImpl implements OrgBizService {
    private final IOrgService orgService;

    public OrgBizServiceImpl(IOrgService orgService) {
        this.orgService = orgService;
    }

    private OrgDTO toDTO(OrgDO d) {
        if (d == null) return null;
        OrgDTO dto = new OrgDTO();
        BeanUtils.copyProperties(d, dto);
        return dto;
    }

    @Override
    public IPage<OrgDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize) {
        Page<OrgDO> page = new Page<>(pageNum, pageSize);
        IPage<OrgDO> result = orgService.pageByTenantCode(page, tenantCode);
        return result.convert(this::toDTO);
    }

    @Override
    public List<OrgDTO> listByTenantCode(String tenantCode) {
        return orgService.listByTenantCode(tenantCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrgDTO> listByDomainCode(String domainCode) {
        return orgService.listByDomainCode(domainCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public OrgDTO getByOrgCode(String orgCode) {
        return toDTO(orgService.getByOrgCode(orgCode));
    }

    @Override
    public List<OrgDTO> list() {
        return orgService.list().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String orgCode) {
        return orgService.deleteByOrgCode(orgCode);
    }

    @Override
    public boolean create(OrgDTO dto) {
        OrgDO d = new OrgDO();
        BeanUtils.copyProperties(dto, d);
        return orgService.save(d);
    }

    @Override
    public boolean update(OrgDTO dto) {
        OrgDO d = new OrgDO();
        BeanUtils.copyProperties(dto, d);
        return orgService.updateById(d);
    }
}
