package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.AuditLogDTO;

import java.util.List;

public interface AuditBizService {
    IPage<AuditLogDTO> page(AuditLogDTO query, int pageNum, int pageSize);
    List<AuditLogDTO> list(AuditLogDTO query);
    AuditLogDTO getById(Long id);
    void save(AuditLogDTO auditLog);
    void delete(Long id);
    byte[] export(AuditLogDTO query);
}
