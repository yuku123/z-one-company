package com.zifang.z.agent.center.core.agent.instance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.center.core.agent.instance.entity.AgentInstance;
import com.zifang.z.agent.center.core.agent.instance.mapper.AgentInstanceMapper;
import com.zifang.z.agent.center.core.agent.instance.service.AgentInstanceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgentInstanceServiceImpl extends ServiceImpl<AgentInstanceMapper, AgentInstance> implements AgentInstanceService {

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
}
