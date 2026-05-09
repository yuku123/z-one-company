package com.zifang.z.agent.mcp.web.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zifang.z.agent.mcp.web.domain.entity.McpServerConfigDO;
import com.zifang.z.agent.mcp.web.domain.mapper.McpServerConfigMapper;
import com.zifang.z.agent.mcp.web.domain.service.IMcpServerConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class McpServerConfigServiceImpl implements IMcpServerConfigService {

    @Resource
    private McpServerConfigMapper mapper;

    @Override
    public List<McpServerConfigDO> listByTenant(String tenantCode) {
        QueryWrapper<McpServerConfigDO> qw = new QueryWrapper<>();
        qw.eq("tenant_code", tenantCode);
        qw.orderByDesc("gmt_create");
        return mapper.selectList(qw);
    }

    @Override
    public McpServerConfigDO getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public McpServerConfigDO getByServerName(String serverName) {
        QueryWrapper<McpServerConfigDO> qw = new QueryWrapper<>();
        qw.eq("server_name", serverName);
        return mapper.selectOne(qw);
    }

    @Override
    public boolean save(McpServerConfigDO config) {
        config.setGmtCreate(LocalDateTime.now());
        config.setGmtUpdate(LocalDateTime.now());
        if (config.getStatus() == null) config.setStatus("active");
        if (config.getTimeout() == null) config.setTimeout(60);
        return mapper.insert(config) > 0;
    }

    @Override
    public boolean updateById(McpServerConfigDO config) {
        config.setGmtUpdate(LocalDateTime.now());
        return mapper.updateById(config) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return mapper.deleteById(id) > 0;
    }

    @Override
    public List<McpServerConfigDO> listActive() {
        QueryWrapper<McpServerConfigDO> qw = new QueryWrapper<>();
        qw.eq("status", "active");
        return mapper.selectList(qw);
    }
}
