package com.zifang.z.agent.center.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.center.core.agent.conversation.dto.ChatDto;
import com.zifang.z.agent.center.core.agent.conversation.service.AgentConversationService;
import com.zifang.z.agent.center.core.agent.runtime.service.AgentRuntimeService;
import com.zifang.z.agent.center.core.agent.conversation.dto.AgentChatReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "Agent对话")
@RestController
@RequestMapping("/api/agent/chat")
public class AgentChatController {

    @Resource
    private AgentRuntimeService runtimeService;

    @Resource
    private AgentConversationService conversationService;

    @Operation(summary = "发送对话消息")
    @PostMapping("/send")
    public Result<ChatDto> send(@RequestBody AgentChatReq req) {
        return Result.success(runtimeService.chatResp(
                req.getInstanceCode(),
                req.getMessage(),
                req.getUserId(),
                req.getUserName()
        ));
    }

    @Operation(summary = "查询历史消息")
    @GetMapping("/history")
    public Result<List<ChatDto>> history(@RequestParam String instanceCode, @RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(conversationService.listRespByInstance(instanceCode, limit));
    }

    @Operation(summary = "清除会话历史")
    @PostMapping("/clear")
    public Result<Void> clear(@RequestParam String instanceCode) {
        runtimeService.clearHistory(instanceCode);
        return Result.success();
    }
}
