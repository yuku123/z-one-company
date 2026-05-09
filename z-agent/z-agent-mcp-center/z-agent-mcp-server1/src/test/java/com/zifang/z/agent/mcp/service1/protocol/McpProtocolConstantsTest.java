package com.zifang.z.agent.mcp.service1.protocol;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MCP 协议常量测试
 */
public class McpProtocolConstantsTest {

    @Test
    public void testProtocolVersion() {
        assertEquals("2024-11-05", McpProtocolConstants.PROTOCOL_VERSION);
        assertEquals("2.0", McpProtocolConstants.JSON_RPC_VERSION);
    }

    @Test
    public void testEndpointPaths() {
        assertEquals("/mcp", McpProtocolConstants.ENDPOINT_MCP);
        assertEquals("/v1/mcp", McpProtocolConstants.ENDPOINT_MCP_V1);
        assertEquals("/sse", McpProtocolConstants.ENDPOINT_SSE);
        assertEquals("/stream", McpProtocolConstants.ENDPOINT_STREAM);
    }

    @Test
    public void testStandardMethods() {
        // Tools
        assertEquals("tools/list", McpProtocolConstants.METHOD_TOOLS_LIST);
        assertEquals("tools/call", McpProtocolConstants.METHOD_TOOLS_CALL);

        // Resources
        assertEquals("resources/list", McpProtocolConstants.METHOD_RESOURCES_LIST);
        assertEquals("resources/read", McpProtocolConstants.METHOD_RESOURCES_READ);

        // Prompts
        assertEquals("prompts/list", McpProtocolConstants.METHOD_PROMPTS_LIST);
        assertEquals("prompts/get", McpProtocolConstants.METHOD_PROMPTS_GET);

        // Utility
        assertEquals("initialize", McpProtocolConstants.METHOD_INITIALIZE);
        assertEquals("ping", McpProtocolConstants.METHOD_PING);
    }

    @Test
    public void testJsonRpcErrorCodes() {
        assertEquals(-32700, McpProtocolConstants.ERROR_PARSE_ERROR);
        assertEquals(-32600, McpProtocolConstants.ERROR_INVALID_REQUEST);
        assertEquals(-32601, McpProtocolConstants.ERROR_METHOD_NOT_FOUND);
        assertEquals(-32602, McpProtocolConstants.ERROR_INVALID_PARAMS);
        assertEquals(-32603, McpProtocolConstants.ERROR_INTERNAL_ERROR);
    }

    @Test
    public void testToolTypes() {
        assertEquals("BUILT_IN", McpProtocolConstants.TOOL_TYPE_BUILT_IN);
        assertEquals("THIRD_PARTY", McpProtocolConstants.TOOL_TYPE_THIRD_PARTY);
    }

    @Test
    public void testSessionStatuses() {
        assertEquals("initializing", McpProtocolConstants.SESSION_STATUS_INITIALIZING);
        assertEquals("active", McpProtocolConstants.SESSION_STATUS_ACTIVE);
        assertEquals("closing", McpProtocolConstants.SESSION_STATUS_CLOSING);
        assertEquals("closed", McpProtocolConstants.SESSION_STATUS_CLOSED);
    }
}
