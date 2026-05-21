package com.zifang.z.agent.engine.agent.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.engine.agent.share.dto.AgentShareDto;
import com.zifang.z.agent.engine.agent.share.entity.AgentShare;
import com.zifang.z.agent.engine.agent.share.mapper.AgentShareMapper;
import com.zifang.z.agent.engine.agent.share.service.AgentShareService;
import com.zifang.z.agent.engine.app.entity.AgentApp;
import com.zifang.z.agent.engine.app.service.AgentAppService;
import com.zifang.z.agent.engine.agent.share.dto.AgentShareDto;
import com.zifang.z.agent.engine.agent.share.dto.AgentShareReq;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentShareServiceImpl extends ServiceImpl<AgentShareMapper, AgentShare> implements AgentShareService {

    @Resource
    private AgentAppService agentAppService;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AgentShareDto createResp(AgentShareReq req) {
        return toDto(createShare(req.getInstanceCode(), req.getAppCode()));
    }

    @Override
    public AgentShareDto verifyResp(String shareCode) {
        AgentShare share = getByShareCode(shareCode);
        if (share != null) {
            incrementVisitCount(shareCode);
        }
        return toDto(share);
    }

    @Override
    public List<AgentShareDto> listRespByInstance(String instanceCode) {
        return listByInstance(instanceCode).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public AgentShare getByShareCode(String shareCode) {
        LambdaQueryWrapper<AgentShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentShare::getShareCode, shareCode);
        wrapper.eq(AgentShare::getStatus, "ACTIVE");
        AgentShare share = this.getOne(wrapper);
        if (share != null && share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            share.setStatus("EXPIRED");
            this.updateById(share);
            return null;
        }
        return share;
    }

    @Override
    public AgentShare createShare(String instanceCode, String appCode) {
        AgentShare share = new AgentShare();
        share.setShareCode(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        share.setInstanceCode(instanceCode);
        share.setAppCode(appCode);
        share.setShareType("LINK");
        share.setStatus("ACTIVE");
        share.setVisitCount(0);
        share.setGmtCreate(LocalDateTime.now());
        share.setGmtModified(LocalDateTime.now());
        this.save(share);
        return share;
    }

    @Override
    public List<AgentShare> listByInstance(String instanceCode) {
        LambdaQueryWrapper<AgentShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentShare::getInstanceCode, instanceCode);
        wrapper.orderByDesc(AgentShare::getGmtCreate);
        return this.list(wrapper);
    }

    @Override
    public void disable(String shareCode) {
        LambdaQueryWrapper<AgentShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentShare::getShareCode, shareCode);
        AgentShare share = this.getOne(wrapper);
        if (share != null) {
            share.setStatus("DISABLED");
            share.setGmtModified(LocalDateTime.now());
            this.updateById(share);
        }
    }

    @Override
    public void incrementVisitCount(String shareCode) {
        LambdaQueryWrapper<AgentShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentShare::getShareCode, shareCode);
        AgentShare share = this.getOne(wrapper);
        if (share != null) {
            share.setVisitCount(share.getVisitCount() == null ? 1 : share.getVisitCount() + 1);
            share.setGmtModified(LocalDateTime.now());
            this.updateById(share);
        }
    }

    private AgentShareDto toDto(AgentShare share) {
        if (share == null) return null;
        AgentShareDto dto = new AgentShareDto();
        BeanUtils.copyProperties(share, dto);
        dto.setShareUrl("/share/" + share.getShareCode());
        dto.setGmtCreate(share.getGmtCreate() != null ? share.getGmtCreate().format(DF) : null);
        dto.setExpireTime(share.getExpireTime() != null ? share.getExpireTime().format(DF) : null);
        // 补充应用信息
        try {
            AgentApp app = agentAppService.getByAppCode(share.getAppCode());
            if (app != null) {
                dto.setAppName(app.getAppName());
                dto.setAppDesc(app.getDescription());
            }
        } catch (Exception e) {
            // ignore
        }
        return dto;
    }
}
