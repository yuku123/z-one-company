package com.zifang.ctc.core.service.dto.converter;

import org.springframework.beans.BeanUtils;
import com.zifang.ctc.core.domain.entity.GroupDO;
import com.zifang.ctc.core.service.dto.GroupDTO;

public class GroupDtoConverter {
    public static GroupDTO toDTO(GroupDO entity) {
        if (entity == null) return null;
        GroupDTO dto = new GroupDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
