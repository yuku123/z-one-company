package com.zifang.ctc.core.service.dto.converter;

import com.zifang.ctc.core.domain.entity.Permission;
import com.zifang.ctc.core.service.dto.PermissionDTO;
import org.springframework.beans.BeanUtils;

/**
 * Permission DO <-> DTO 转换器
 */
public class PermissionDtoConverter {

    private PermissionDtoConverter() {}

    public static PermissionDTO toDTO(Permission entity) {
        if (entity == null) return null;
        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static Permission toEntity(PermissionDTO dto) {
        if (dto == null) return null;
        Permission entity = new Permission();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
