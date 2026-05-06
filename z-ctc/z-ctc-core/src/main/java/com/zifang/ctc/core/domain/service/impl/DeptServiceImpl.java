package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.DeptDO;
import com.zifang.ctc.core.domain.mapper.DeptMapper;
import com.zifang.ctc.core.domain.service.IDeptService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, DeptDO> implements IDeptService {
    @Override
    public IPage<DeptDO> pageByTenantCode(Page<DeptDO> page, String tenantCode) {
        LambdaQueryWrapper<DeptDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(DeptDO::getTenantCode, tenantCode);
        return page(page, w);
    }

    @Override
    public List<DeptDO> listByTenantCode(String tenantCode) {
        LambdaQueryWrapper<DeptDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(DeptDO::getTenantCode, tenantCode);
        return list(w);
    }

    @Override
    public List<DeptDO> listByDomainCode(String domainCode) {
        LambdaQueryWrapper<DeptDO> w = new LambdaQueryWrapper<>();
        if (domainCode != null) w.eq(DeptDO::getDomainCode, domainCode);
        return list(w);
    }

    @Override
    public List<DeptDO> listByOrgCode(String orgCode) {
        LambdaQueryWrapper<DeptDO> w = new LambdaQueryWrapper<>();
        if (orgCode != null) w.eq(DeptDO::getOrgCode, orgCode);
        return list(w);
    }

    @Override
    public DeptDO getByDeptCode(String deptCode) {
        return getOne(new LambdaQueryWrapper<DeptDO>().eq(DeptDO::getDeptCode, deptCode));
    }

    @Override
    public Long getIdByDeptCode(String deptCode) {
        DeptDO d = getByDeptCode(deptCode);
        return d != null ? d.getId() : null;
    }

    @Override
    public boolean deleteByDeptCode(String deptCode) {
        Long id = getIdByDeptCode(deptCode);
        return id != null && removeById(id);
    }
}
