package com.zifang.z.agent.center.core.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.center.core.app.dto.AgentAppDto;
import com.zifang.z.agent.center.core.app.dto.AgentAppReq;
import com.zifang.z.agent.center.core.app.entity.AgentApp;
import com.zifang.z.agent.center.core.app.entity.AgentAppVersion;

import java.util.List;

public interface AgentAppService {

    IPage<AgentAppDto> pageResp(String keyword, String status, int pageNum, int pageSize);

    AgentAppDto getRespByAppCode(String appCode);

    AgentAppDto createResp(AgentAppReq req, ObjectMapper objectMapper);

    AgentAppDto updateResp(AgentAppReq req, ObjectMapper objectMapper);

    AgentApp getByAppCode(String appCode);

    AgentApp create(AgentApp app);

    AgentApp update(AgentApp app);

    void remove(Long id);

    void publish(String appCode);

    List<AgentAppDto> listVersionResp(String appCode);

    List<AgentAppVersion> listVersions(String appCode);

    AgentAppVersion createVersion(AgentAppVersion version);

    String getDraft(String appCode);

    void saveDraft(String appCode, String draftData);

    void incrementVisitCount(String instanceCode);
}
