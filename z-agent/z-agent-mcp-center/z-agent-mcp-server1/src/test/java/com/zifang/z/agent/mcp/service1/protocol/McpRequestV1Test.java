package com.zifang.z.agent.mcp.service1.protocol;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * MCP v1 请求测试
 */
public class McpRequestV1Test {

    @Test
    public void testDefaultValues() {
        McpRequestV1 request = new McpRequestV1();

        assertEquals("2.0", request.getJsonrpc());
        assertNull(request.getId());
        assertNull(request.getMethod());
        assertNull(request.getParams());
    }

    @Test
    public void testSettersAndGetters() {
        McpRequestV1 request = new McpRequestV1();

        // Test id
        request.setId("req-123");
        assertEquals("req-123", request.getId());

        // Test method
        request.setMethod("tools/list");
        assertEquals("tools/list", request.getMethod());

        // Test params
        Map<String, Object> params = new HashMap<>();
        params.put("filter", "BUILT_IN");
        request.setParams(params);
        assertEquals(params, request.getParams());
    }

    @Test
    public void testInitializeRequest() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("init-1");
        request.setMethod("initialize");

        Map<String, Object> params = new HashMap<>();
        params.put("protocolVersion", "2024-11-05");

        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("tools", new HashMap<>());
        params.put("capabilities", capabilities);

        request.setParams(params);

        assertEquals("init-1", request.getId());
        assertEquals("initialize", request.getMethod());
        assertNotNull(request.getParams());
        assertEquals("2024-11-05", request.getParams().get("protocolVersion"));
    }

    @Test
    public void testToolsListRequest() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("tools-1");
        request.setMethod("tools/list");

        Map<String, Object> params = new HashMap<>();
        params.put("cursor", "cursor-123");
        params.put("limit", 20);
        request.setParams(params);

        assertEquals("tools/list", request.getMethod());
        assertEquals("cursor-123", request.getParams().get("cursor"));
        assertEquals(20, request.getParams().get("limit"));
    }

    @Test
    public void testToolsCallRequest() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("call-1");
        request.setMethod("tools/call");

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", "test query");
        arguments.put("limit", 10);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "search_tool");
        params.put("arguments", arguments);

        request.setParams(params);

        assertEquals("tools/call", request.getMethod());
        assertEquals("search_tool", request.getParams().get("name"));
        @SuppressWarnings("unchecked")
        Map<String, Object> args = (Map<String, Object>) request.getParams().get("arguments");
        assertEquals("test query", args.get("query"));
    }

    @Test
    public void testPingRequest() {
        McpRequestV1 request = new McpRequestV1();
        request.setId("ping-1");
        request.setMethod("ping");

        assertEquals("ping", request.getMethod());
        assertEquals("ping-1", request.getId());
    }
}
