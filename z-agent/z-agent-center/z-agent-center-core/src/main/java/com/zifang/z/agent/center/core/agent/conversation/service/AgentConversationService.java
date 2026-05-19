package com.zifang.z.agent.center.core.agent.conversation.service;

import com.zifang.z.agent.center.core.agent.conversation.entity.AgentConversation;

import java.util.List;

public interface AgentConversationService {

    boolean saveEntity(AgentConversation conversation);

    List<AgentConversation> listByInstance(String instanceCode, int limit);

    List<AgentConversation> listByConversationCode(String conversationCode);
}
