package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.OrgDO;
import com.zifang.ctc.core.domain.mapper.OrgMapper;
import com.zifang.ctc.core.domain.service.IOrgService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, OrgDO> implements IOrgService {

    @Override
    public IPage<OrgDO> page(Page<OrgDO> page, OrgDO orgDO) {
        LambdaQueryWrapper<OrgDO> wrapper = new LambdaQueryWrapper<>();
        if (orgDO != null) {
            if (orgDO.getId() != null) {
                wrapper.eq(OrgDO::getId, orgDO.getId());
            }
            if (StringUtils.hasText(orgDO.getOrgName())) {
                wrapper.like(OrgDO::getOrgName, orgDO.getOrgName());
            }
            if (orgDO.getTenantId() != null) {
                wrapper.eq(OrgDO::getTenantId, orgDO.getTenantId());
            }
            if (orgDO.getDomainId() != null) {
                wrapper.eq(OrgDO::getDomainId, orgDO.getDomainId());
            }
            if (orgDO.getStatus() != null) {
                wrapper.eq(OrgDO::getStatus, orgDO.getStatus());
            }
        }
        wrapper.orderByDesc(OrgDO::getCreatedTime);
        return page(page, wrapper);
    }

    @Override
    public List<OrgDO> listByTenantId(Long tenantId) {
        return list(new LambdaQueryWrapper<OrgDO>()
                .eq(OrgDO::getTenantId, tenantId)
                .orderByDesc(OrgDO::getCreatedTime));
    }

    @Override
    public List<OrgDO> listByDomainId(Long domainId) {
        return list(new LambdaQueryWrapper<OrgDO>()
                .eq(OrgDO::getDomainId, domainId)
                .orderByDesc(OrgDO::getCreatedTime));
    }

    @Override
    public OrgDO getByOrgId(Long orgId) {
        return getOne(new LambdaQueryWrapper<OrgDO>().eq(OrgDO::getId, orgId));
    }

    @Override
    public boolean add(OrgDO orgDO) {
        orgDO.setCreatedTime(LocalDateTime.now());
        orgDO.setUpdatedTime(LocalDateTime.now());
        return save(orgDO);
    }

    @Override
    public boolean update(OrgDO orgDO) {
        orgDO.setUpdatedTime(LocalDateTime.now());
        return updateById(orgDO);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
