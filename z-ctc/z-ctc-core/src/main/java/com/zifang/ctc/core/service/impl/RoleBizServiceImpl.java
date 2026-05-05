package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.service.model.request.RolePageReq;
import com.zifang.ctc.core.domain.service.IPermissionService;
import com.zifang.ctc.core.domain.service.IRoleService;
import com.zifang.ctc.core.service.RoleBizService;
import com.zifang.ctc.core.service.dto.PermissionDTO;
import com.zifang.ctc.core.service.dto.RoleDTO;
import com.zifang.ctc.core.service.dto.converter.PermissionDtoConverter;
import com.zifang.ctc.core.service.dto.converter.RoleDtoConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleBizServiceImpl implements RoleBizService {

    @Resource
    private IRoleService roleService;

    @Resource
    private IPermissionService permissionService;

    @Override
    public List<RoleDTO> list() {
        return roleService.list(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getStatus, 1)
                        .orderByDesc(Role::getGmtCreate)
        ).stream()
                .map(RoleDtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<RoleDTO> page(RolePageReq req) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .orderByDesc(Role::getGmtCreate);

        if (req.getRoleName() != null && !req.getRoleName().isEmpty()) {
            wrapper.like(Role::getRoleName, req.getRoleName());
        }
        if (req.getRoleCode() != null && !req.getRoleCode().isEmpty()) {
            wrapper.like(Role::getRoleCode, req.getRoleCode());
        }
        if (req.getStatus() != null) {
            wrapper.eq(Role::getStatus, req.getStatus());
        }

        Page<Role> page = new Page<>(req.getCurrent(), req.getSize());
        return roleService.page(page, wrapper).convert(RoleDtoConverter::toDTO);
    }

    @Override
    public RoleDTO getById(Long id) {
        return RoleDtoConverter.toDTO(roleService.getById(id));
    }

    @Override
    @Transactional
    public void create(Role role) {
        if (roleService.selectByRoleCode(role.getRoleCode()) != null) {
            throw new RuntimeException("角色编码已存在");
        }

        role.setStatus(1);
        role.setGmtCreate(LocalDateTime.now());
        role.setGmtModified(LocalDateTime.now());

        roleService.save(role);
    }

    @Override
    @Transactional
    public void update(Role role) {
        Role existingRole = roleService.getById(role.getId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }

        role.setRoleCode(null);
        role.setStatus(null);
        role.setGmtModified(LocalDateTime.now());

        roleService.updateById(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(0);
        role.setGmtModified(LocalDateTime.now());
        roleService.updateById(role);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
    }

    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        return permissionService.selectPermissionsByRoleId(roleId).stream()
                .map(PermissionDtoConverter::toDTO)
                .collect(Collectors.toList());
    }
}
