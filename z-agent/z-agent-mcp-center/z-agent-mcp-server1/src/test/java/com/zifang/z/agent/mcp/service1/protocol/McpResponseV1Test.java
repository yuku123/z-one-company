package com.zifang.z.agent.mcp.service1.protocol;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * MCP v1 响应测试
 */
public class McpResponseV1Test {

    @Test
    public void testDefaultValues() {
        McpResponseV1 response = new McpResponseV1();

        assertEquals("2.0", response.getJsonrpc());
        assertNull(response.getId());
        assertNull(response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void testSuccessResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", new java.util.ArrayList<>());
        result.put("nextCursor", null);

        McpResponseV1 response = McpResponseV1.success("req-123", result);

        assertEquals("2.0", response.getJsonrpc());
        assertEquals("req-123", response.getId());
        assertNotNull(response.getResult());
        assertNull(response.getError());

        @SuppressWarnings("unchecked")
        Map<String, Object> res = (Map<String, Object>) response.getResult();
        assertNotNull(res.get("tools"));
    }

    @Test
    public void testErrorResponse() {
        McpResponseV1 response = McpResponseV1.error("req-456", -32600, "Invalid Request");

        assertEquals("2.0", response.getJsonrpc());
        assertEquals("req-456", response.getId());
        assertNull(response.getResult());
        assertNotNull(response.getError());

        assertEquals(-32600, response.getError().getCode());
        assertEquals("Invalid Request", response.getError().getMessage());
    }

    @Test
    public void testErrorResponseWithData() {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("field", "method");
        errorData.put("reason", "unknown");

        McpResponseV1 response = McpResponseV1.error("req-789", -32601, "Method not found", errorData);

        assertEquals(-32601, response.getError().getCode());
        assertNotNull(response.getError().getData());
    }

    @Test
    public void testToolsListResponse() {
        java.util.List<Map<String, Object>> tools = new java.util.ArrayList<>();

        Map<String, Object> tool1 = new HashMap<>();
        tool1.put("name", "search");
        tool1.put("description", "Search tool");
        tools.add(tool1);

        Map<String, Object> result = new HashMap<>();
        result.put("tools", tools);
        result.put("nextCursor", null);

        McpResponseV1 response = McpResponseV1.success("tools-req-1", result);

        assertNotNull(response.getResult());
        @SuppressWarnings("unchecked")
        Map<String, Object> res = (Map<String, Object>) response.getResult();
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> toolsList = (java.util.List<Map<String, Object>>) res.get("tools");
        assertEquals(1, toolsList.size());
        assertEquals("search", toolsList.get(0).get("name"));
    }

    @Test
    public void testInitializeResponse() {
        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("name", "test-server");
        serverInfo.put("version", "1.0.0");

        Map<String, Object> capabilities = new HashMap<>();
        Map<String, Object> tools = new HashMap<>();
        tools.put("listChanged", true);
        capabilities.put("tools", tools);

        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("serverInfo", serverInfo);
        result.put("capabilities", capabilities);

        McpResponseV1 response = McpResponseV1.success("init-1", result);

        assertNotNull(response.getResult());
        @SuppressWarnings("unchecked")
        Map<String, Object> res = (Map<String, Object>) response.getResult();
        assertEquals("2024-11-05", res.get("protocolVersion"));

        @SuppressWarnings("unchecked")
        Map<String, Object> caps = (Map<String, Object>) res.get("capabilities");
        assertNotNull(caps.get("tools"));
    }

    @Test
    public void testErrorCodes() {
        // 标准 JSON-RPC 2.0 错误码
        assertEquals(-32700, McpProtocolConstants.ERROR_PARSE_ERROR);
        assertEquals(-32600, McpProtocolConstants.ERROR_INVALID_REQUEST);
        assertEquals(-32601, McpProtocolConstants.ERROR_METHOD_NOT_FOUND);
        assertEquals(-32602, McpProtocolConstants.ERROR_INVALID_PARAMS);
        assertEquals(-32603, McpProtocolConstants.ERROR_INTERNAL_ERROR);
    }
}
