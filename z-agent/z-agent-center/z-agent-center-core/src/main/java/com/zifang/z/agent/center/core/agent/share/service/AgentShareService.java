package com.zifang.z.agent.center.core.agent.share.service;

import com.zifang.z.agent.center.core.agent.share.entity.AgentShare;

import java.util.List;

public interface AgentShareService {

    AgentShare getByShareCode(String shareCode);

    AgentShare createShare(String instanceCode, String appCode);

    List<AgentShare> listByInstance(String instanceCode);

    void disable(String shareCode);

    void incrementVisitCount(String shareCode);
}
