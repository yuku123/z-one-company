package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.GroupDO;
import com.zifang.ctc.core.domain.service.IGroupService;
import com.zifang.ctc.core.service.GroupBizService;
import com.zifang.ctc.core.service.dto.GroupDTO;
import com.zifang.ctc.core.service.dto.converter.GroupDtoConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupBizServiceImpl implements GroupBizService {

    @Resource
    private IGroupService groupService;

    @Override
    public List<GroupDTO> list() {
        return groupService.list().stream()
                .map(GroupDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public IPage<GroupDTO> page(GroupDTO dto) {
        LambdaQueryWrapper<GroupDO> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            if (dto.getGroupName() != null && !dto.getGroupName().isEmpty())
                wrapper.like(GroupDO::getGroupName, dto.getGroupName());
            if (dto.getTenantId() != null) wrapper.eq(GroupDO::getTenantId, dto.getTenantId());
            if (dto.getDomainId() != null) wrapper.eq(GroupDO::getDomainId, dto.getDomainId());
            if (dto.getOrgId() != null) wrapper.eq(GroupDO::getOrgId, dto.getOrgId());
            if (dto.getDeptId() != null) wrapper.eq(GroupDO::getDeptId, dto.getDeptId());
            if (dto.getStatus() != null) wrapper.eq(GroupDO::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(GroupDO::getCreatedTime);
        Page<GroupDO> p = new Page<>(1, 10);
        return groupService.page(p, wrapper).convert(GroupDtoConverter::toDTO);
    }

    @Override
    public List<GroupDTO> listByTenantId(Long tenantId) {
        return groupService.listByTenantId(tenantId).stream()
                .map(GroupDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GroupDTO> listByDomainId(Long domainId) {
        return groupService.listByDomainId(domainId).stream()
                .map(GroupDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GroupDTO> listByOrgId(Long orgId) {
        return groupService.listByOrgId(orgId).stream()
                .map(GroupDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<GroupDTO> listByDeptId(Long deptId) {
        return groupService.listByDeptId(deptId).stream()
                .map(GroupDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public GroupDTO getById(Long id) {
        return GroupDtoConverter.toDTO(groupService.getById(id));
    }

    @Override
    public void add(GroupDTO dto) {
        GroupDO entity = new GroupDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        groupService.add(entity);
    }

    @Override
    public void update(GroupDTO dto) {
        GroupDO entity = new GroupDO();
        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        groupService.update(entity);
    }

    @Override
    public void delete(Long id) {
        groupService.delete(id);
    }
}
