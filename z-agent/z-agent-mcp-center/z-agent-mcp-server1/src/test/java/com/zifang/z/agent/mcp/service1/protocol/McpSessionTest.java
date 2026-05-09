package com.zifang.z.agent.mcp.service1.protocol;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * MCP 会话测试
 */
public class McpSessionTest {

    @Test
    public void testDefaultConstructor() {
        McpSession session = new McpSession();

        assertNotNull(session.getSessionId());
        assertEquals(McpProtocolConstants.SESSION_STATUS_INITIALIZING, session.getStatus());
        assertTrue(session.getCreateTime() > 0);
        assertTrue(session.getLastActivityTime() > 0);
        assertNotNull(session.getSubscribedResources());
        assertNotNull(session.getProgressTrackers());
        assertNotNull(session.getMetadata());
    }

    @Test
    public void testSettersAndGetters() {
        McpSession session = new McpSession();

        session.setSessionId("test-session-123");
        assertEquals("test-session-123", session.getSessionId());

        session.setStatus(McpProtocolConstants.SESSION_STATUS_ACTIVE);
        assertEquals(McpProtocolConstants.SESSION_STATUS_ACTIVE, session.getStatus());

        session.setProtocolVersion("2024-11-05");
        assertEquals("2024-11-05", session.getProtocolVersion());

        McpSession.ClientCapabilities caps = new McpSession.ClientCapabilities();
        session.setClientCapabilities(caps);
        assertEquals(caps, session.getClientCapabilities());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");
        session.setMetadata(metadata);
        assertEquals(metadata, session.getMetadata());
    }

    @Test
    public void testUpdateActivity() throws InterruptedException {
        McpSession session = new McpSession();
        long before = session.getLastActivityTime();

        Thread.sleep(10); // Sleep briefly to ensure time changes

        session.updateActivity();
        long after = session.getLastActivityTime();

        assertTrue(after > before);
    }

    @Test
    public void testIsActive() {
        McpSession session = new McpSession();

        // Initially not active
        assertFalse(session.isActive());

        // Set to active
        session.setStatus(McpProtocolConstants.SESSION_STATUS_ACTIVE);
        assertTrue(session.isActive());

        // Set to closed
        session.setStatus(McpProtocolConstants.SESSION_STATUS_CLOSED);
        assertFalse(session.isActive());
    }

    @Test
    public void testResourceSubscription() {
        McpSession session = new McpSession();

        String resourceUri = "file:///test/resource.txt";

        // Initially not subscribed
        assertFalse(session.isSubscribedTo(resourceUri));

        // Subscribe
        session.subscribeResource(resourceUri);
        assertTrue(session.isSubscribedTo(resourceUri));

        // Unsubscribe
        session.unsubscribeResource(resourceUri);
        assertFalse(session.isSubscribedTo(resourceUri));
    }

    @Test
    public void testProgressTracker() {
        McpSession session = new McpSession();

        String token = "progress-123";
        double total = 100.0;

        // Create tracker
        McpSession.ProgressTracker tracker = session.createProgressTracker(token, total);

        assertNotNull(tracker);
        assertEquals(token, tracker.getToken());
        assertEquals(total, tracker.getTotal(), 0.001);
        assertEquals(0.0, tracker.getProgress(), 0.001);

        // Update progress
        session.updateProgress(token, 50.0, "Halfway there");
        assertEquals(50.0, tracker.getProgress(), 0.001);
        assertEquals("Halfway there", tracker.getMessage());

        // Remove tracker
        session.removeProgressTracker(token);
        // Should not throw
        session.updateProgress(token, 75.0, "Should not update");
    }

    @Test
    public void testClientCapabilities() {
        McpSession.ClientCapabilities caps = new McpSession.ClientCapabilities();

        // Test sampling
        McpSession.SamplingCapability sampling = new McpSession.SamplingCapability();
        sampling.setEnabled(true);
        caps.setSampling(sampling);
        assertEquals(sampling, caps.getSampling());

        // Test roots
        McpSession.RootsCapability roots = new McpSession.RootsCapability();
        roots.setListChanged(true);
        caps.setRoots(roots);
        assertEquals(roots, caps.getRoots());

        // Test experimental
        Map<String, Object> experimental = new HashMap<>();
        experimental.put("feature1", true);
        caps.setExperimental(experimental);
        assertEquals(experimental, caps.getExperimental());
    }
}
