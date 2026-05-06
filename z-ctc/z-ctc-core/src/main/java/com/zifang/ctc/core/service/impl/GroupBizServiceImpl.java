package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.GroupDO;
import com.zifang.ctc.core.domain.service.IGroupService;
import com.zifang.ctc.core.service.GroupBizService;
import com.zifang.ctc.core.service.dto.GroupDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupBizServiceImpl implements GroupBizService {
    private final IGroupService groupService;

    public GroupBizServiceImpl(IGroupService groupService) {
        this.groupService = groupService;
    }

    private GroupDTO toDTO(GroupDO d) {
        if (d == null) return null;
        GroupDTO dto = new GroupDTO();
        BeanUtils.copyProperties(d, dto);
        return dto;
    }

    @Override
    public IPage<GroupDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize) {
        Page<GroupDO> page = new Page<>(pageNum, pageSize);
        IPage<GroupDO> result = groupService.pageByTenantCode(page, tenantCode);
        return result.convert(this::toDTO);
    }

    @Override
    public List<GroupDTO> listByTenantCode(String tenantCode) {
        return groupService.listByTenantCode(tenantCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GroupDTO> listByDomainCode(String domainCode) {
        return groupService.listByDomainCode(domainCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GroupDTO> listByOrgCode(String orgCode) {
        return groupService.listByOrgCode(orgCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GroupDTO> listByDeptCode(String deptCode) {
        return groupService.listByDeptCode(deptCode).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public GroupDTO getByGroupCode(String groupCode) {
        return toDTO(groupService.getByGroupCode(groupCode));
    }

    @Override
    public List<GroupDTO> list() {
        return groupService.list().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String groupCode) {
        return groupService.deleteByGroupCode(groupCode);
    }

    @Override
    public boolean create(GroupDTO dto) {
        GroupDO d = new GroupDO();
        BeanUtils.copyProperties(dto, d);
        return groupService.save(d);
    }

    @Override
    public boolean update(GroupDTO dto) {
        GroupDO d = new GroupDO();
        BeanUtils.copyProperties(dto, d);
        return groupService.updateById(d);
    }
}
