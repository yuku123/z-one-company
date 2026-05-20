package com.zifang.z.agent.center.core.agent.instance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.center.core.agent.instance.dto.AgentInstanceDto;
import com.zifang.z.agent.center.core.agent.instance.entity.AgentInstance;
import com.zifang.z.agent.center.core.agent.instance.mapper.AgentInstanceMapper;
import com.zifang.z.agent.center.core.agent.instance.service.AgentInstanceService;
import com.zifang.z.agent.center.core.agent.instance.dto.AgentInstanceDto;
import com.zifang.z.agent.center.core.agent.instance.dto.AgentInstanceReq;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentInstanceServiceImpl extends ServiceImpl<AgentInstanceMapper, AgentInstance> implements AgentInstanceService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AgentInstanceDto createResp(AgentInstanceReq req) {
        AgentInstance instance = createFromApp(req.getAppCode(), req.getUserId(), req.getUserName());
        return toDto(instance);
    }

    @Override
    public AgentInstanceDto getRespByInstanceCode(String instanceCode) {
        return toDto(getByInstanceCode(instanceCode));
    }

    @Override
    public List<AgentInstanceDto> listRespByOwner(String ownerId) {
        return listByOwner(ownerId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public AgentInstance getByInstanceCode(String instanceCode) {
        LambdaQueryWrapper<AgentInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentInstance::getInstanceCode, instanceCode);
        wrapper.eq(AgentInstance::getIsDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public AgentInstance createFromApp(String appCode, String userId, String userName) {
        AgentInstance instance = new AgentInstance();
        instance.setInstanceCode("inst_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        instance.setAppCode(appCode);
        instance.setOwnerId(userId);
        instance.setOwnerName(userName);
        instance.setInstanceName("我的" + appCode);
        instance.setStatus("ACTIVE");
        instance.setVisitCount(0);
        instance.setGmtCreate(LocalDateTime.now());
        instance.setGmtModified(LocalDateTime.now());
        instance.setIsDeleted(0);
        this.save(instance);
        return instance;
    }

    @Override
    public List<AgentInstance> listByOwner(String ownerId) {
        LambdaQueryWrapper<AgentInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentInstance::getOwnerId, ownerId);
        wrapper.eq(AgentInstance::getIsDeleted, 0);
        wrapper.orderByDesc(AgentInstance::getGmtCreate);
        return this.list(wrapper);
    }

    @Override
    public void updateStatus(String instanceCode, String status) {
        AgentInstance instance = getByInstanceCode(instanceCode);
        if (instance != null) {
            instance.setStatus(status);
            instance.setGmtModified(LocalDateTime.now());
            this.updateById(instance);
        }
    }

    @Override
    public void recordVisit(String instanceCode) {
        AgentInstance instance = getByInstanceCode(instanceCode);
        if (instance != null) {
            instance.setVisitCount(instance.getVisitCount() == null ? 1 : instance.getVisitCount() + 1);
            instance.setLastVisitTime(LocalDateTime.now());
            instance.setGmtModified(LocalDateTime.now());
            this.updateById(instance);
        }
    }

    private AgentInstanceDto toDto(AgentInstance instance) {
        if (instance == null) return null;
        AgentInstanceDto dto = new AgentInstanceDto();
        BeanUtils.copyProperties(instance, dto);
        dto.setLastVisitTime(instance.getLastVisitTime() != null ? instance.getLastVisitTime().format(DF) : null);
        return dto;
    }
}
