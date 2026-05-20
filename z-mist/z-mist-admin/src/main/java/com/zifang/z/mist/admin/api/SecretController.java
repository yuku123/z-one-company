package com.zifang.z.mist.admin.api;

import com.zifang.z.mist.common.Constance;
import com.zifang.z.mist.core.domain.entity.ZMistSecretInfo;
import com.zifang.z.mist.core.domain.service.IZMistSecretService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 密钥管理控制器
 */
@Tag(name = "密钥管理")
@RestController
@RequestMapping("/api/secret")
public class SecretController {

    @Autowired
    private IZMistSecretService secretService;

    @Operation(summary = "保存密钥")
    @PostMapping
    public Map<String, Object> saveSecret(@RequestBody ZMistSecretInfo secret) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置默认值
            if (secret.getNamespace() == null) {
                secret.setNamespace(Constance.DEFAULT_NAMESPACE);
            }
            if (secret.getGroup() == null) {
                secret.setGroup(Constance.DEFAULT_GROUP);
            }
            if (secret.getEncryptAlgorithm() == null) {
                secret.setEncryptAlgorithm(Constance.EncryptAlgorithm.AES);
            }
            if (secret.getSecretType() == null) {
                secret.setSecretType(Constance.SecretType.TEXT);
            }

            ZMistSecretInfo saved = secretService.saveSecret(secret);
            result.put("success", true);
            result.put("data", saved);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Operation(summary = "更新密钥")
    @PutMapping
    public Map<String, Object> updateSecret(@RequestBody ZMistSecretInfo secret) {
        Map<String, Object> result = new HashMap<>();
        try {
            ZMistSecretInfo updated = secretService.updateSecret(secret);
            result.put("success", true);
            result.put("data", updated);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Operation(summary = "删除密钥")
    @DeleteMapping("/{secretKey}")
    public Map<String, Object> deleteSecret(
            @PathVariable String secretKey,
            @RequestParam(required = false, defaultValue = "DEFAULT_GROUP") String group,
            @RequestParam(required = false, defaultValue = "") String namespace) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean deleted = secretService.deleteSecret(secretKey, group, namespace);
            result.put("success", deleted);
            result.put("message", deleted ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Operation(summary = "获取密钥详情")
    @GetMapping("/get")
    public Map<String, Object> getSecret(
            @RequestParam String secretKey,
            @RequestParam(required = false, defaultValue = "DEFAULT_GROUP") String group,
            @RequestParam(required = false, defaultValue = "") String namespace) {
        Map<String, Object> result = new HashMap<>();
        try {
            ZMistSecretInfo secret = secretService.getSecret(secretKey, group, namespace);
            result.put("success", secret != null);
            result.put("data", secret);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Operation(summary = "密钥列表")
    @GetMapping("/list")
    public Map<String, Object> listSecrets(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String appName,
            @RequestParam(required = false, defaultValue = "") String namespace) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ZMistSecretInfo> list = secretService.listSecrets(group, appName, namespace);
            result.put("success", true);
            result.put("data", list);
            result.put("total", list.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}