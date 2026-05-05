package com.zifang.ctc.core.service.dto.converter;

import com.zifang.ctc.core.domain.entity.Role;
import com.zifang.ctc.core.service.dto.RoleDTO;
import org.springframework.beans.BeanUtils;

/**
 * Role DO <-> DTO 转换器
 */
public class RoleDtoConverter {

    private RoleDtoConverter() {}

    public static RoleDTO toDTO(Role entity) {
        if (entity == null) return null;
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static Role toEntity(RoleDTO dto) {
        if (dto == null) return null;
        Role entity = new Role();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
