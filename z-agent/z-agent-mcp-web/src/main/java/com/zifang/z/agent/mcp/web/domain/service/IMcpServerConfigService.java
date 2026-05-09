package com.zifang.z.agent.mcp.web.domain.service;

import com.zifang.z.agent.mcp.web.domain.entity.McpServerConfigDO;
import java.util.List;

public interface IMcpServerConfigService {

    List<McpServerConfigDO> listByTenant(String tenantCode);

    McpServerConfigDO getById(Long id);

    McpServerConfigDO getByServerName(String serverName);

    boolean save(McpServerConfigDO config);

    boolean updateById(McpServerConfigDO config);

    boolean removeById(Long id);

    List<McpServerConfigDO> listActive();
}
