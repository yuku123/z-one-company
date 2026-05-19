package com.zifang.z.agent.center.core.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.z.agent.center.core.app.entity.AgentApp;
import com.zifang.z.agent.center.core.app.entity.AgentAppVersion;

import java.util.List;

public interface AgentAppService {

    IPage<AgentApp> page(String keyword, String status, int pageNum, int pageSize);

    AgentApp getByAppCode(String appCode);

    AgentApp create(AgentApp app);

    AgentApp update(AgentApp app);

    void delete(Long id);

    void publish(String appCode);

    List<AgentAppVersion> listVersions(String appCode);

    AgentAppVersion createVersion(AgentAppVersion version);

    String getDraft(String appCode);

    void saveDraft(String appCode, String draftData);

    void incrementVisitCount(String instanceCode);
}
