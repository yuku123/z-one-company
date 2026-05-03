package com.zifang.z.agent.mcp.starter;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * MCP 注册中心测试
 */
public class McpRegistryTest {

    private McpRegistry registry;

    @Before
    public void setUp() {
        registry = new McpRegistry();
    }

    @Test
    public void testRegisterBuiltInTool() {
        ToolMeta tool = createToolMeta("search", "BUILT_IN", "Search tool");

        boolean result = registry.registerTool(tool);

        assertTrue(result);
        assertNotNull(registry.getToolMeta("search"));
        assertTrue(registry.isBuiltInTool("search"));
    }

    @Test
    public void testRegisterThirdPartyTool() {
        ToolMeta tool = createToolMeta("calculator", "THIRD_PARTY", "Calculator tool");

        boolean result = registry.registerTool(tool);

        assertTrue(result);
        assertNotNull(registry.getToolMeta("calculator"));
        assertFalse(registry.isBuiltInTool("calculator"));
    }

    @Test
    public void testRegisterDuplicateTool() {
        ToolMeta tool1 = createToolMeta("unique", "BUILT_IN", "First");
        ToolMeta tool2 = createToolMeta("unique", "THIRD_PARTY", "Second");

        boolean first = registry.registerTool(tool1);
        boolean second = registry.registerTool(tool2);

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    public void testUnregisterThirdPartyTool() {
        ToolMeta tool = createToolMeta("removable", "THIRD_PARTY", "Removable tool");
        registry.registerTool(tool);

        boolean result = registry.unregisterTool("removable");

        assertTrue(result);
        assertNull(registry.getToolMeta("removable"));
    }

    @Test
    public void testUnregisterBuiltInTool() {
        ToolMeta tool = createToolMeta("permanent", "BUILT_IN", "Permanent tool");
        registry.registerTool(tool);

        boolean result = registry.unregisterTool("permanent");

        assertFalse(result);
        assertNotNull(registry.getToolMeta("permanent"));
    }

    @Test
    public void testUnregisterNonExistentTool() {
        boolean result = registry.unregisterTool("nonexistent");
        assertFalse(result);
    }

    @Test
    public void testListAllTools() {
        registry.registerTool(createToolMeta("tool1", "BUILT_IN", "Tool 1"));
        registry.registerTool(createToolMeta("tool2", "THIRD_PARTY", "Tool 2"));
        registry.registerTool(createToolMeta("tool3", "BUILT_IN", "Tool 3"));

        List<ToolMeta> tools = registry.listTools(null);

        assertEquals(3, tools.size());
    }

    @Test
    public void testListBuiltInTools() {
        registry.registerTool(createToolMeta("tool1", "BUILT_IN", "Tool 1"));
        registry.registerTool(createToolMeta("tool2", "THIRD_PARTY", "Tool 2"));
        registry.registerTool(createToolMeta("tool3", "BUILT_IN", "Tool 3"));

        List<ToolMeta> tools = registry.listTools("BUILT_IN");

        assertEquals(2, tools.size());
        for (ToolMeta tool : tools) {
            assertEquals("BUILT_IN", tool.getType());
        }
    }

    @Test
    public void testListThirdPartyTools() {
        registry.registerTool(createToolMeta("tool1", "BUILT_IN", "Tool 1"));
        registry.registerTool(createToolMeta("tool2", "THIRD_PARTY", "Tool 2"));
        registry.registerTool(createToolMeta("tool3", "THIRD_PARTY", "Tool 3"));

        List<ToolMeta> tools = registry.listTools("THIRD_PARTY");

        assertEquals(2, tools.size());
        for (ToolMeta tool : tools) {
            assertEquals("THIRD_PARTY", tool.getType());
        }
    }

    @Test
    public void testGetToolMeta() {
        ToolMeta tool = createToolMeta("test", "BUILT_IN", "Test tool");
        registry.registerTool(tool);

        ToolMeta retrieved = registry.getToolMeta("test");

        assertNotNull(retrieved);
        assertEquals("test", retrieved.getToolName());
        assertEquals("BUILT_IN", retrieved.getType());
        assertEquals("Test tool", retrieved.getDescription());
    }

    @Test
    public void testGetNonExistentTool() {
        ToolMeta retrieved = registry.getToolMeta("nonexistent");
        assertNull(retrieved);
    }

    @Test
    public void testToolMetaSchema() {
        Map<String, Object> inputSchema = new HashMap<>();
        inputSchema.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("query", new HashMap<String, Object>() {{ put("type", "string"); }});
        inputSchema.put("properties", properties);

        ToolMeta tool = new ToolMeta();
        tool.setToolName("search");
        tool.setType("BUILT_IN");
        tool.setDescription("Search tool");
        tool.setInputSchema(inputSchema);
        tool.setExecuteUrl("http://localhost:8080/search");
        tool.setAuthToken("secret-token");

        assertEquals("search", tool.getToolName());
        assertEquals("BUILT_IN", tool.getType());
        assertEquals("Search tool", tool.getDescription());
        assertEquals(inputSchema, tool.getInputSchema());
        assertEquals("http://localhost:8080/search", tool.getExecuteUrl());
        assertEquals("secret-token", tool.getAuthToken());
        assertTrue(tool.getCreateTime() > 0);
    }

    private ToolMeta createToolMeta(String name, String type, String description) {
        ToolMeta tool = new ToolMeta();
        tool.setToolName(name);
        tool.setType(type);
        tool.setDescription(description);
        return tool;
    }
}
