package com.zifang.z.agent.engine.agent.share.service;

import com.zifang.z.agent.engine.agent.share.dto.AgentShareDto;
import com.zifang.z.agent.engine.agent.share.dto.AgentShareReq;
import com.zifang.z.agent.engine.agent.share.entity.AgentShare;

import java.util.List;

public interface AgentShareService {

    AgentShareDto createResp(AgentShareReq req);

    AgentShareDto verifyResp(String shareCode);

    List<AgentShareDto> listRespByInstance(String instanceCode);

    AgentShare getByShareCode(String shareCode);

    AgentShare createShare(String instanceCode, String appCode);

    List<AgentShare> listByInstance(String instanceCode);

    void disable(String shareCode);

    void incrementVisitCount(String shareCode);
}
