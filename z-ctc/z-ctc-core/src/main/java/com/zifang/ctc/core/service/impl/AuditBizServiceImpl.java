package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.AuditLog;
import com.zifang.ctc.core.domain.mapper.AuditLogMapper;
import com.zifang.ctc.core.service.AuditBizService;
import com.zifang.ctc.core.service.dto.AuditLogDTO;
import com.zifang.ctc.core.service.dto.converter.AuditLogDtoConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditBizServiceImpl implements AuditBizService {

    @Resource
    private AuditLogMapper auditLogMapper;

    @Override
    public IPage<AuditLogDTO> page(AuditLogDTO query, int pageNum, int pageSize) {
        LambdaQueryWrapper<AuditLog> wrapper = buildWrapper(query);
        wrapper.orderByDesc(AuditLog::getGmtCreate);
        Page<AuditLog> p = new Page<>(pageNum, pageSize);
        return auditLogMapper.selectPage(p, wrapper).convert(AuditLogDtoConverter::toDTO);
    }

    @Override
    public List<AuditLogDTO> list(AuditLogDTO query) {
        return auditLogMapper.selectList(buildWrapper(query))
                .stream().map(AuditLogDtoConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public AuditLogDTO getById(Long id) {
        return AuditLogDtoConverter.toDTO(auditLogMapper.selectById(id));
    }

    @Override
    public void save(AuditLogDTO dto) {
        AuditLog entity = new AuditLog();
        BeanUtils.copyProperties(dto, entity);
        auditLogMapper.insert(entity);
    }

    @Override
    public void delete(Long id) {
        auditLogMapper.deleteById(id);
    }

    @Override
    public byte[] export(AuditLogDTO query) {
        List<AuditLogDTO> logs = list(query);
        StringBuilder sb = new StringBuilder();
        sb.append("操作时间,操作类型,操作描述,用户名,IP地址,请求URL,状态\n");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (AuditLogDTO log : logs) {
            sb.append(log.getGmtCreate() != null ? log.getGmtCreate().format(df) : "").append(",");
            sb.append(log.getOperationType()).append(",");
            sb.append(log.getOperationDesc()).append(",");
            sb.append(log.getUserName()).append(",");
            sb.append(log.getIpAddress()).append(",");
            sb.append(log.getRequestUrl()).append(",");
            sb.append(log.getStatus() == 1 ? "成功" : "失败").append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private LambdaQueryWrapper<AuditLog> buildWrapper(AuditLogDTO query) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (query == null) return wrapper;
        if (query.getUserName() != null && !query.getUserName().isEmpty()) {
            wrapper.like(AuditLog::getUserName, query.getUserName());
        }
        if (query.getOperationType() != null && !query.getOperationType().isEmpty()) {
            wrapper.eq(AuditLog::getOperationType, query.getOperationType());
        }
        if (query.getIpAddress() != null && !query.getIpAddress().isEmpty()) {
            wrapper.like(AuditLog::getIpAddress, query.getIpAddress());
        }
        return wrapper;
    }
}
