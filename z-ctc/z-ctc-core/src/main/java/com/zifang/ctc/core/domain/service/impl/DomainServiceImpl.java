package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.DomainDO;
import com.zifang.ctc.core.domain.mapper.DomainMapper;
import com.zifang.ctc.core.domain.service.IDomainService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DomainServiceImpl extends ServiceImpl<DomainMapper, DomainDO> implements IDomainService {
    @Override
    public IPage<DomainDO> pageByTenantCode(Page<DomainDO> page, String tenantCode) {
        LambdaQueryWrapper<DomainDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(DomainDO::getTenantCode, tenantCode);
        return page(page, w);
    }

    @Override
    public List<DomainDO> listByTenantCode(String tenantCode) {
        LambdaQueryWrapper<DomainDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(DomainDO::getTenantCode, tenantCode);
        return list(w);
    }

    @Override
    public DomainDO getByDomainCode(String domainCode) {
        return getOne(new LambdaQueryWrapper<DomainDO>().eq(DomainDO::getDomainCode, domainCode));
    }

    @Override
    public Long getIdByDomainCode(String domainCode) {
        DomainDO d = getByDomainCode(domainCode);
        return d != null ? d.getId() : null;
    }

    @Override
    public boolean deleteByDomainCode(String domainCode) {
        Long id = getIdByDomainCode(domainCode);
        return id != null && removeById(id);
    }
}
