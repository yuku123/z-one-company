package com.zifang.z.agent.center.core.agent.conversation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.center.core.agent.conversation.entity.AgentConversation;
import com.zifang.z.agent.center.core.agent.conversation.mapper.AgentConversationMapper;
import com.zifang.z.agent.center.core.agent.conversation.service.AgentConversationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentConversationServiceImpl extends ServiceImpl<AgentConversationMapper, AgentConversation> implements AgentConversationService {

    @Override
    public boolean saveEntity(AgentConversation conversation) {
        if (conversation.getId() == null) {
            conversation.setGmtCreate(LocalDateTime.now());
            return this.baseMapper.insert(conversation) > 0;
        } else {
            return this.baseMapper.updateById(conversation) > 0;
        }
    }

    @Override
    public List<AgentConversation> listByInstance(String instanceCode, int limit) {
        LambdaQueryWrapper<AgentConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentConversation::getInstanceCode, instanceCode);
        wrapper.orderByDesc(AgentConversation::getGmtCreate);
        wrapper.last("LIMIT " + limit);
        return this.list(wrapper);
    }

    @Override
    public List<AgentConversation> listByConversationCode(String conversationCode) {
        LambdaQueryWrapper<AgentConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentConversation::getConversationCode, conversationCode);
        wrapper.orderByAsc(AgentConversation::getGmtCreate);
        return this.list(wrapper);
    }
}
