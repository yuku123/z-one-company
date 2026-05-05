package com.zifang.ctc.core.service.dto.converter;

import com.zifang.ctc.core.domain.entity.AuditLog;
import com.zifang.ctc.core.service.dto.AuditLogDTO;
import org.springframework.beans.BeanUtils;

public class AuditLogDtoConverter {
    public static AuditLogDTO toDTO(AuditLog entity) {
        if (entity == null) return null;
        AuditLogDTO dto = new AuditLogDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
