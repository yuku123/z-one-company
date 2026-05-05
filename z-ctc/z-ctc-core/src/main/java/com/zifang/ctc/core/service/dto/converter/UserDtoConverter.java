package com.zifang.ctc.core.service.dto.converter;

import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.service.dto.UserDTO;
import org.springframework.beans.BeanUtils;

/**
 * User DO <-> DTO 转换器
 */
public class UserDtoConverter {

    private UserDtoConverter() {}

    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static UserDTO toDTO(User entity, java.util.List<String> roles) {
        if (entity == null) return null;
        UserDTO dto = toDTO(entity);
        dto.setRoles(roles);
        return dto;
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User entity = new User();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
