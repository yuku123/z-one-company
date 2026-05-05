package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.DomainDO;
import com.zifang.ctc.core.domain.mapper.DomainMapper;
import com.zifang.ctc.core.domain.service.IDomainService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DomainServiceImpl extends ServiceImpl<DomainMapper, DomainDO> implements IDomainService {

    @Override
    public IPage<DomainDO> page(Page<DomainDO> page, DomainDO domainDO) {
        LambdaQueryWrapper<DomainDO> wrapper = new LambdaQueryWrapper<>();
        if (domainDO != null) {
            if (StringUtils.hasText(domainDO.getDomainCode())) {
                wrapper.like(DomainDO::getDomainCode, domainDO.getDomainCode());
            }
            if (StringUtils.hasText(domainDO.getDomainName())) {
                wrapper.like(DomainDO::getDomainName, domainDO.getDomainName());
            }
            if (domainDO.getTenantId() != null) {
                wrapper.eq(DomainDO::getTenantId, domainDO.getTenantId());
            }
            if (domainDO.getStatus() != null) {
                wrapper.eq(DomainDO::getStatus, domainDO.getStatus());
            }
        }
        wrapper.orderByDesc(DomainDO::getCreatedTime);
        return page(page, wrapper);
    }

    @Override
    public List<DomainDO> listByTenantId(Long tenantId) {
        return list(new LambdaQueryWrapper<DomainDO>()
                .eq(DomainDO::getTenantId, tenantId)
                .orderByDesc(DomainDO::getCreatedTime));
    }

    @Override
    public DomainDO getByDomainCode(String domainCode) {
        return getOne(new LambdaQueryWrapper<DomainDO>().eq(DomainDO::getDomainCode, domainCode));
    }

    @Override
    public boolean add(DomainDO domainDO) {
        domainDO.setCreatedTime(LocalDateTime.now());
        domainDO.setUpdatedTime(LocalDateTime.now());
        return save(domainDO);
    }

    @Override
    public boolean update(DomainDO domainDO) {
        domainDO.setUpdatedTime(LocalDateTime.now());
        return updateById(domainDO);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
