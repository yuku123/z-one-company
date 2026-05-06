package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.OrgDO;
import com.zifang.ctc.core.domain.mapper.OrgMapper;
import com.zifang.ctc.core.domain.service.IOrgService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, OrgDO> implements IOrgService {
    @Override
    public IPage<OrgDO> pageByTenantCode(Page<OrgDO> page, String tenantCode) {
        LambdaQueryWrapper<OrgDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(OrgDO::getTenantCode, tenantCode);
        return page(page, w);
    }

    @Override
    public List<OrgDO> listByTenantCode(String tenantCode) {
        LambdaQueryWrapper<OrgDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(OrgDO::getTenantCode, tenantCode);
        return list(w);
    }

    @Override
    public List<OrgDO> listByDomainCode(String domainCode) {
        LambdaQueryWrapper<OrgDO> w = new LambdaQueryWrapper<>();
        if (domainCode != null) w.eq(OrgDO::getDomainCode, domainCode);
        return list(w);
    }

    @Override
    public OrgDO getByOrgCode(String orgCode) {
        return getOne(new LambdaQueryWrapper<OrgDO>().eq(OrgDO::getOrgCode, orgCode));
    }

    @Override
    public Long getIdByOrgCode(String orgCode) {
        OrgDO o = getByOrgCode(orgCode);
        return o != null ? o.getId() : null;
    }

    @Override
    public boolean deleteByOrgCode(String orgCode) {
        Long id = getIdByOrgCode(orgCode);
        return id != null && removeById(id);
    }
}
