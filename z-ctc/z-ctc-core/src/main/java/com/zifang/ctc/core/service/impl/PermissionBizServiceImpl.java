package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.domain.service.IPermissionService;
import com.zifang.ctc.core.service.PermissionBizService;
import com.zifang.ctc.core.service.dto.PermissionDTO;
import com.zifang.ctc.core.service.dto.converter.PermissionDtoConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionBizServiceImpl implements PermissionBizService {

    @Resource
    private IPermissionService permissionService;

    @Override
    public List<PermissionDTO> list() {
        return permissionService.list(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, 1)
                        .orderByAsc(Permission::getSortOrder)
        ).stream()
                .map(PermissionDtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDTO getById(Long id) {
        return PermissionDtoConverter.toDTO(permissionService.getById(id));
    }

    @Override
    @Transactional
    public void create(Permission permission) {
        if (permissionService.selectByPermCode(permission.getPermCode()) != null) {
            throw new RuntimeException("权限编码已存在");
        }

        permission.setStatus(1);
        permission.setGmtCreate(LocalDateTime.now());
        permission.setGmtModified(LocalDateTime.now());

        permissionService.save(permission);
    }

    @Override
    @Transactional
    public void update(Permission permission) {
        Permission existingPermission = permissionService.getById(permission.getId());
        if (existingPermission == null) {
            throw new RuntimeException("权限不存在");
        }

        permission.setPermCode(null);
        permission.setStatus(null);
        permission.setGmtModified(LocalDateTime.now());

        permissionService.updateById(permission);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setStatus(0);
        permission.setGmtModified(LocalDateTime.now());
        permissionService.updateById(permission);
    }

    @Override
    public List<PermissionDTO> getByParentId(Long parentId) {
        return permissionService.selectByParentId(parentId).stream()
                .map(PermissionDtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getUserPermissions(Long userId) {
        return permissionService.selectPermissionsByUserId(userId).stream()
                .map(PermissionDtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        return permissionService.selectPermissionsByRoleId(roleId).stream()
                .map(PermissionDtoConverter::toDTO)
                .collect(Collectors.toList());
    }
}
