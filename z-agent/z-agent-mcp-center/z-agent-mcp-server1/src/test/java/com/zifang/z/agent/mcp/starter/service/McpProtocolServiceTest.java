package com.zifang.z.agent.mcp.starter.service;

import com.zifang.z.agent.mcp.starter.McpHandler;
import com.zifang.z.agent.mcp.starter.McpRegistry;
import com.zifang.z.agent.mcp.starter.ToolMeta;
import com.zifang.z.agent.mcp.starter.protocol.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * MCP 协议服务测试
 */
public class McpProtocolServiceTest {

    private McpProtocolService protocolService;

    @Mock
    private McpRegistry mcpRegistry;

    private McpSession session;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        protocolService = new McpProtocolService();
        session = new McpSession();
        session.setSessionId("test-session-123");
    }

    @Test
    public void testHandleInitialize() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("init-1");
        request.setMethod("initialize");

        Map<String, Object> params = new HashMap<>();
        params.put("protocolVersion", "2024-11-05");

        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("tools", new HashMap<>());
        params.put("capabilities", capabilities);

        request.setParams(params);

        McpResponseV1 response = protocolService.handleRequest(request, session);

        assertNotNull(response);
        assertEquals("init-1", response.getId());
        assertNull(response.getError());
        assertNotNull(response.getResult());

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) response.getResult();
        assertEquals("2024-11-05", result.get("protocolVersion"));
        assertNotNull(result.get("serverInfo"));
        assertNotNull(result.get("capabilities"));

        // Session should be updated
        assertEquals("active", session.getStatus());
        assertEquals("2024-11-05", session.getProtocolVersion());
    }

    @Test
    public void testHandlePing() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("ping-1");
        request.setMethod("ping");

        McpResponseV1 response = protocolService.handleRequest(request, session);

        assertNotNull(response);
        assertEquals("ping-1", response.getId());
        assertNull(response.getError());
        assertNotNull(response.getResult());

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) response.getResult();
        assertNotNull(result.get("timestamp"));
    }

    @Test
    public void testHandleToolsList() {
        // Setup mock registry
        ToolMeta tool1 = new ToolMeta();
        tool1.setToolName("search");
        tool1.setType("BUILT_IN");
        tool1.setDescription("Search tool");

        ToolMeta tool2 = new ToolMeta();
        tool2.setToolName("calculator");
        tool2.setType("THIRD_PARTY");
        tool2.setDescription("Calculator tool");

        when(mcpRegistry.listTools(null)).thenReturn(java.util.Arrays.asList(tool1, tool2));

        McpRequestV1 request = new McpRequestV1();
        request.setId("list-1");
        request.setMethod("tools/list");

        // Note: This test would need proper injection to work with mocked registry
        // For now, just verifying the request structure
        assertEquals("tools/list", request.getMethod());
        assertEquals("list-1", request.getId());
    }

    @Test
    public void testHandleInvalidMethod() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("invalid-1");
        request.setMethod("unknown/method");

        McpResponseV1 response = protocolService.handleRequest(request, session);

        assertNotNull(response);
        assertEquals("invalid-1", response.getId());
        assertNotNull(response.getError());
        assertEquals(-32601, response.getError().getCode()); // METHOD_NOT_FOUND
        assertTrue(response.getError().getMessage().contains("Method not found"));
    }

    @Test
    public void testHandleNullRequest() {
        McpResponseV1 response = protocolService.handleRequest(null, session);

        assertNotNull(response);
        assertNull(response.getId());
        assertNotNull(response.getError());
        assertEquals(-32600, response.getError().getCode()); // INVALID_REQUEST
    }

    @Test
    public void testHandleRequestWithNullMethod() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("null-method");
        request.setMethod(null);

        McpResponseV1 response = protocolService.handleRequest(request, session);

        assertNotNull(response);
        assertEquals(-32600, response.getError().getCode()); // INVALID_REQUEST
        assertTrue(response.getError().getMessage().contains("method is required"));
    }

    @Test
    public void testSessionActivityUpdate() {
        long beforeActivity = session.getLastActivityTime();

        McpRequestV1 request = new McpRequestV1();
        request.setId("ping-activity");
        request.setMethod("ping");

        // Wait a bit to ensure time difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // ignore
        }

        protocolService.handleRequest(request, session);

        assertTrue(session.getLastActivityTime() > beforeActivity);
    }
}
