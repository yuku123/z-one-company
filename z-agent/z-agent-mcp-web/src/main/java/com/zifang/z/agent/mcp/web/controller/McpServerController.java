package com.zifang.z.agent.mcp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.mcp.web.R;
import com.zifang.z.agent.mcp.web.domain.entity.McpServerConfigDO;
import com.zifang.z.agent.mcp.web.domain.service.IMcpServerConfigService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/mcp/server")
public class McpServerController {

    private static final Logger log = LoggerFactory.getLogger(McpServerController.class);
    private static final ObjectMapper om = new ObjectMapper();
    private static final OkHttpClient http = new OkHttpClient();

    @Resource
    private IMcpServerConfigService svc;

    // ===== CRUD =====

    @GetMapping("/list")
    public R<List<McpServerConfigDO>> list(@RequestParam(required = false) String tenantCode) {
        if (tenantCode == null) tenantCode = "default";
        return R.success(svc.listByTenant(tenantCode));
    }

    @GetMapping("/{id}")
    public R<McpServerConfigDO> get(@PathVariable Long id) {
        return R.success(svc.getById(id));
    }

    @PostMapping
    public R<Void> create(@RequestBody McpServerConfigDO config) {
        if (config.getTenantCode() == null) config.setTenantCode("default");
        svc.save(config);
        return R.success();
    }

    @PostMapping("/update")
    public R<Void> update(@RequestBody McpServerConfigDO config) {
        svc.updateById(config);
        return R.success();
    }

    @PostMapping("/{id}/delete")
    public R<Void> delete(@PathVariable Long id) {
        svc.removeById(id);
        return R.success();
    }

    // ===== 工具代理 =====

    @PostMapping("/{id}/tools/list")
    public R<Map<String, Object>> proxyToolsList(@PathVariable Long id) {
        McpServerConfigDO s = svc.getById(id);
        if (s == null) return R.fail("not found");
        return proxyCall(s, buildReq("tools/list", null));
    }

    @PostMapping("/{id}/tools/call")
    public R<Map<String, Object>> proxyToolsCall(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        McpServerConfigDO s = svc.getById(id);
        if (s == null) return R.fail("not found");
        Map<String, Object> cp = new LinkedHashMap<>();
        cp.put("name", params.get("name"));
        cp.put("arguments", params.getOrDefault("arguments", Collections.emptyMap()));
        return proxyCall(s, buildReq("tools/call", cp));
    }

    @PostMapping("/{id}/test")
    public R<Map<String, Object>> testConnection(@PathVariable Long id) {
        McpServerConfigDO s = svc.getById(id);
        if (s == null) return R.fail("not found");

        Map<String, Object> result = new LinkedHashMap<>();

        // init params
        Map<String, Object> ip = new LinkedHashMap<>();
        ip.put("protocolVersion", "2024-11-05");
        ip.put("capabilities", Collections.emptyMap());
        Map<String, Object> ci = new LinkedHashMap<>();
        ci.put("name", "mcp-web");
        ci.put("version", "1.0");
        ip.put("clientInfo", ci);

        Map<String, Object> initResp = send(s, buildReq("initialize", ip));
        if (initResp == null) {
            result.put("connected", false);
            return R.success(result);
        }
        Map<String, Object> initR = getMap(initResp, "result");
        result.put("connected", true);
        if (initR != null) {
            result.put("serverInfo", initR.get("serverInfo"));
            result.put("protocolVersion", initR.get("protocolVersion"));
        }

        Map<String, Object> listResp = send(s, buildReq("tools/list", null));
        Map<String, Object> listR = getMap(listResp, "result");
        if (listR != null && listR.get("tools") instanceof List) {
            result.put("toolCount", ((List<?>) listR.get("tools")).size());
        }

        return R.success(result);
    }

    // ===== 内部方法 =====

    private R<Map<String, Object>> proxyCall(McpServerConfigDO s, Map<String, Object> req) {
        Map<String, Object> resp = send(s, req);
        if (resp == null) return R.fail("connection failed");
        if (resp.containsKey("error")) return R.fail(resp.get("error").toString());
        return R.success(getMap(resp, "result"));
    }

    private Map<String, Object> buildReq(String method, Map<String, Object> params) {
        Map<String, Object> req = new LinkedHashMap<>();
        req.put("jsonrpc", "2.0");
        req.put("id", 1);
        req.put("method", method);
        req.put("params", params != null ? params : Collections.emptyMap());
        return req;
    }

    private Map<String, Object> send(McpServerConfigDO s, Map<String, Object> req) {
        try {
            String json = om.writeValueAsString(req);
            Request.Builder b = new Request.Builder()
                    .url(s.getUrl())
                    .post(okhttp3.RequestBody.create(json, MediaType.parse("application/json")));
            if (s.getAuthToken() != null && !s.getAuthToken().isEmpty()) {
                b.addHeader("Authorization", "Bearer " + s.getAuthToken());
            }
            try (Response r = http.newCall(b.build()).execute()) {
                String body = r.body() != null ? r.body().string() : "{}";
                return om.readValue(body, Map.class);
            }
        } catch (IOException e) {
            log.error("MCP send failed: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> m, String key) {
        Object v = m != null ? m.get(key) : null;
        return v instanceof Map ? (Map<String, Object>) v : null;
    }
}
