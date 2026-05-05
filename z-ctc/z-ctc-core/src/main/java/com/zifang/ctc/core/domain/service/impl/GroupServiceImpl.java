package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.GroupDO;
import com.zifang.ctc.core.domain.mapper.GroupMapper;
import com.zifang.ctc.core.domain.service.IGroupService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements IGroupService {

    @Override
    public IPage<GroupDO> page(Page<GroupDO> page, GroupDO groupDO) {
        LambdaQueryWrapper<GroupDO> wrapper = new LambdaQueryWrapper<>();
        if (groupDO != null) {

            if (StringUtils.hasText(groupDO.getGroupName())) {
                wrapper.like(GroupDO::getGroupName, groupDO.getGroupName());
            }
            if (groupDO.getTenantId() != null) {
                wrapper.eq(GroupDO::getTenantId, groupDO.getTenantId());
            }
            if (groupDO.getDomainId() != null) {
                wrapper.eq(GroupDO::getDomainId, groupDO.getDomainId());
            }
            if (groupDO.getOrgId() != null) {
                wrapper.eq(GroupDO::getOrgId, groupDO.getOrgId());
            }
            if (groupDO.getDeptId() != null) {
                wrapper.eq(GroupDO::getDeptId, groupDO.getDeptId());
            }
            if (groupDO.getStatus() != null) {
                wrapper.eq(GroupDO::getStatus, groupDO.getStatus());
            }
        }
        wrapper.orderByDesc(GroupDO::getCreatedTime);
        return page(page, wrapper);
    }

    @Override
    public List<GroupDO> listByTenantId(Long tenantId) {
        return list(new LambdaQueryWrapper<GroupDO>()
                .eq(GroupDO::getTenantId, tenantId)
                .orderByDesc(GroupDO::getCreatedTime));
    }

    @Override
    public List<GroupDO> listByDomainId(Long domainId) {
        return list(new LambdaQueryWrapper<GroupDO>()
                .eq(GroupDO::getDomainId, domainId)
                .orderByDesc(GroupDO::getCreatedTime));
    }

    @Override
    public List<GroupDO> listByOrgId(Long orgId) {
        return list(new LambdaQueryWrapper<GroupDO>()
                .eq(GroupDO::getOrgId, orgId)
                .orderByDesc(GroupDO::getCreatedTime));
    }

    @Override
    public List<GroupDO> listByDeptId(Long deptId) {
        return list(new LambdaQueryWrapper<GroupDO>()
                .eq(GroupDO::getDeptId, deptId)
                .orderByDesc(GroupDO::getCreatedTime));
    }

    @Override
    public GroupDO getByGroupId(Long groupId) {
        return getOne(new LambdaQueryWrapper<GroupDO>().eq(GroupDO::getId, groupId));
    }

    @Override
    public boolean add(GroupDO groupDO) {
        groupDO.setCreatedTime(LocalDateTime.now());
        groupDO.setUpdatedTime(LocalDateTime.now());
        return save(groupDO);
    }

    @Override
    public boolean update(GroupDO groupDO) {
        groupDO.setUpdatedTime(LocalDateTime.now());
        return updateById(groupDO);
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}
