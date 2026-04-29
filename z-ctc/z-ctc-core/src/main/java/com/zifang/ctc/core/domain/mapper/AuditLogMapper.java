package com.zifang.ctc.core.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.ctc.core.domain.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志 Mapper 接口
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据操作人ID查询日志
     */
    List<AuditLog> selectByOperatorId(@Param("operatorId") Long operatorId);

    /**
     * 根据操作对象查询日志
     */
    List<AuditLog> selectByTarget(@Param("targetType") String targetType, @Param("targetId") String targetId);

    /**
     * 根据操作类型查询日志
     */
    List<AuditLog> selectByOperationType(@Param("operationType") String operationType);

    /**
     * 根据时间范围查询日志
     */
    List<AuditLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
