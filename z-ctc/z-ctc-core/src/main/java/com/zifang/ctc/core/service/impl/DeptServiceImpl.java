package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.DeptDO;
import com.zifang.ctc.core.domain.mapper.DeptMapper;
import com.zifang.ctc.core.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, DeptDO> implements DeptService {

    @Override
    public IPage<DeptDO> page(Page<DeptDO> page, DeptDO deptDO) {
        LambdaQueryWrapper<DeptDO> wrapper = new LambdaQueryWrapper<>();
        if (deptDO != null) {
            if (deptDO.getId() != null) {
                wrapper.eq(DeptDO::getId, deptDO.getId());
            }
            if (StringUtils.hasText(deptDO.getDeptName())) {
                wrapper.like(DeptDO::getDeptName, deptDO.getDeptName());
            }
            if (deptDO.getTenantId() != null) {
                wrapper.eq(DeptDO::getTenantId, deptDO.getTenantId());
            }
            if (deptDO.getDomainId() != null) {
                wrapper.eq(DeptDO::getDomainId, deptDO.getDomainId());
            }
            if (deptDO.getOrgId() != null) {
                wrapper.eq(DeptDO::getOrgId, deptDO.getOrgId());
            }
            if (deptDO.getStatus() != null) {
                wrapper.eq(DeptDO::getStatus, deptDO.getStatus());
            }
        }
        wrapper.orderByDesc(DeptDO::getCreatedTime);
        return page(page, wrapper);
    }

    @Override
    public List<DeptDO> listByTenantId(Long tenantId) {
        return list(new LambdaQueryWrapper<DeptDO>()
                .eq(DeptDO::getTenantId, tenantId)
                .orderByDesc(DeptDO::getCreatedTime));
    }

    @Override
    public List<DeptDO> listByDomainId(Long domainId) {
        return list(new LambdaQueryWrapper<DeptDO>()
                .eq(DeptDO::getDomainId, domainId)
                .orderByDesc(DeptDO::getCreatedTime));
    }

    @Override
    public List<DeptDO> listByOrgId(Long orgId) {
        return list(new LambdaQueryWrapper<DeptDO>()
                .eq(DeptDO::getOrgId, orgId)
                .orderByDesc(DeptDO::getCreatedTime));
    }

    @Override
    public DeptDO getByDeptId(Long deptId) {
        return getOne(new LambdaQueryWrapper<DeptDO>().eq(DeptDO::getId, deptId));
    }

    @Override
    public boolean add(DeptDO deptDO) {
        deptDO.setCreatedTime(LocalDateTime.now());
        deptDO.setUpdatedTime(LocalDateTime.now());
        return save(deptDO);
    }

    @Override
    public boolean update(DeptDO deptDO) {
        deptDO.setUpdatedTime(LocalDateTime.now());
        return updateById(deptDO);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
