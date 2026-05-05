package com.zifang.ctc.core.service.dto.converter;

import org.springframework.beans.BeanUtils;
import com.zifang.ctc.core.domain.entity.DomainDO;
import com.zifang.ctc.core.service.dto.DomainDTO;

public class DomainDtoConverter {
    public static DomainDTO toDTO(DomainDO entity) {
        if (entity == null) return null;
        DomainDTO dto = new DomainDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
