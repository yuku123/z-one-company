package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.GroupDO;
import com.zifang.ctc.core.domain.mapper.GroupMapper;
import com.zifang.ctc.core.domain.service.IGroupService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements IGroupService {
    @Override
    public IPage<GroupDO> pageByTenantCode(Page<GroupDO> page, String tenantCode) {
        LambdaQueryWrapper<GroupDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(GroupDO::getTenantCode, tenantCode);
        return page(page, w);
    }

    @Override
    public List<GroupDO> listByTenantCode(String tenantCode) {
        LambdaQueryWrapper<GroupDO> w = new LambdaQueryWrapper<>();
        if (tenantCode != null) w.eq(GroupDO::getTenantCode, tenantCode);
        return list(w);
    }

    @Override
    public List<GroupDO> listByDomainCode(String domainCode) {
        LambdaQueryWrapper<GroupDO> w = new LambdaQueryWrapper<>();
        if (domainCode != null) w.eq(GroupDO::getDomainCode, domainCode);
        return list(w);
    }

    @Override
    public List<GroupDO> listByOrgCode(String orgCode) {
        LambdaQueryWrapper<GroupDO> w = new LambdaQueryWrapper<>();
        if (orgCode != null) w.eq(GroupDO::getOrgCode, orgCode);
        return list(w);
    }

    @Override
    public List<GroupDO> listByDeptCode(String deptCode) {
        LambdaQueryWrapper<GroupDO> w = new LambdaQueryWrapper<>();
        if (deptCode != null) w.eq(GroupDO::getDeptCode, deptCode);
        return list(w);
    }

    @Override
    public GroupDO getByGroupCode(String groupCode) {
        return getOne(new LambdaQueryWrapper<GroupDO>().eq(GroupDO::getGroupCode, groupCode));
    }

    @Override
    public Long getIdByGroupCode(String groupCode) {
        GroupDO g = getByGroupCode(groupCode);
        return g != null ? g.getId() : null;
    }

    @Override
    public boolean deleteByGroupCode(String groupCode) {
        Long id = getIdByGroupCode(groupCode);
        return id != null && removeById(id);
    }
}
