package com.zifang.z.agent.center.core.agent.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.center.core.agent.share.entity.AgentShare;
import com.zifang.z.agent.center.core.agent.share.mapper.AgentShareMapper;
import com.zifang.z.agent.center.core.agent.share.service.AgentShareService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgentShareServiceImpl extends ServiceImpl<AgentShareMapper, AgentShare> implements AgentShareService {

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
        AgentShare share = new AgentShare();
        share.setShareCode(shareCode);
        share.setStatus("DISABLED");
        share.setGmtModified(LocalDateTime.now());
        this.updateById(share);
    }

    @Override
    public void incrementVisitCount(String shareCode) {
        AgentShare share = new AgentShare();
        share.setShareCode(shareCode);
        share.setVisitCount(1);
        this.updateById(share);
    }
}
