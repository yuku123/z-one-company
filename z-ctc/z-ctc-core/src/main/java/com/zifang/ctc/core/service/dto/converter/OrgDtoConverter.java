package com.zifang.ctc.core.service.dto.converter;

import org.springframework.beans.BeanUtils;
import com.zifang.ctc.core.domain.entity.OrgDO;
import com.zifang.ctc.core.service.dto.OrgDTO;

public class OrgDtoConverter {
    public static OrgDTO toDTO(OrgDO entity) {
        if (entity == null) return null;
        OrgDTO dto = new OrgDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
