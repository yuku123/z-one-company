package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.AuditBizService;
import com.zifang.ctc.core.service.dto.AuditLogDTO;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.AuditLogReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Tag(name = "审计日志")
@RestController
@RequestMapping("/api/audit")
public class AuditManagerController {

    @Resource
    private AuditBizService auditBizService;

    @Operation(summary = "分页查询")
    @GetMapping("/log")
    public Result<IPage<AuditLogDTO>> page(AuditLogReq req) {
        AuditLogDTO query = new AuditLogDTO();
        if (req.getUserName() != null) query.setUserName(req.getUserName());
        if (req.getOperationType() != null) query.setOperationType(req.getOperationType());
        if (req.getIpAddress() != null) query.setIpAddress(req.getIpAddress());
        int pageNum = req.getCurrent() != null ? req.getCurrent() : 1;
        int pageSize = req.getPageSize() != null ? req.getPageSize() : 10;
        return Result.success(auditBizService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "导出")
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(AuditLogReq req) {
        AuditLogDTO query = new AuditLogDTO();
        if (req.getUserName() != null) query.setUserName(req.getUserName());
        if (req.getOperationType() != null) query.setOperationType(req.getOperationType());
        if (req.getIpAddress() != null) query.setIpAddress(req.getIpAddress());
        byte[] data = auditBizService.export(query);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=audit_log.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(data);
    }
}
