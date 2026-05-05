package com.zifang.ctc.core.service.dto.converter;

import org.springframework.beans.BeanUtils;
import com.zifang.ctc.core.domain.entity.Tenant;
import com.zifang.ctc.core.service.dto.TenantDTO;

public class TenantDtoConverter {
    public static TenantDTO toDTO(Tenant entity) {
        if (entity == null) return null;
        TenantDTO dto = new TenantDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
