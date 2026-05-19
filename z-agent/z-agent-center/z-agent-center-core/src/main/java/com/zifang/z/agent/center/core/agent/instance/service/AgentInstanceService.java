package com.zifang.z.agent.center.core.agent.instance.service;

import com.zifang.z.agent.center.core.agent.instance.entity.AgentInstance;

import java.util.List;

public interface AgentInstanceService {

    AgentInstance getByInstanceCode(String instanceCode);

    AgentInstance createFromApp(String appCode, String userId, String userName);

    List<AgentInstance> listByOwner(String ownerId);

    void updateStatus(String instanceCode, String status);

    void recordVisit(String instanceCode);
}
