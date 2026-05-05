package com.zifang.ctc.core.service.dto.converter;

import org.springframework.beans.BeanUtils;
import com.zifang.ctc.core.domain.entity.DeptDO;
import com.zifang.ctc.core.service.dto.DeptDTO;

public class DeptDtoConverter {
    public static DeptDTO toDTO(DeptDO entity) {
        if (entity == null) return null;
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
