package com.zifang.z.mist.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.z.mist.common.Constance;
import com.zifang.z.mist.core.domain.entity.ZMistSecretHistory;
import com.zifang.z.mist.core.domain.entity.ZMistSecretInfo;
import com.zifang.z.mist.core.domain.mapper.ZMistSecretHistoryMapper;
import com.zifang.z.mist.core.domain.mapper.ZMistSecretInfoMapper;
import com.zifang.z.mist.core.domain.service.IZMistSecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 密钥服务实现
 */
@Service
@org.springframework.context.annotation.Primary
public class ZMistSecretServiceImpl implements IZMistSecretService {

    @Autowired
    private ZMistSecretInfoMapper secretInfoMapper;

    @Autowired
    private ZMistSecretHistoryMapper secretHistoryMapper;

    private static final String DEFAULT_MASTER_KEY = "z-mist-default-master-key-2024";

    @Override
    public ZMistSecretInfo saveSecret(ZMistSecretInfo secret) {
        LocalDateTime now = LocalDateTime.now();
        secret.setGmtCreate(now);
        secret.setGmtModified(now);
        secret.setKeyVersion("v1");

        // 加密密钥值
        String encryptedValue = encryptValue(secret.getEncryptedValue(), secret.getEncryptAlgorithm());
        secret.setEncryptedValue(encryptedValue);

        // 计算 MD5
        String md5 = calculateMd5(secret.getEncryptedValue());
        secret.setValueMd5(md5);

        secretInfoMapper.insert(secret);

        // 保存历史记录
        saveHistory(secret, "INSERT");

        return secret;
    }

    @Override
    public ZMistSecretInfo updateSecret(ZMistSecretInfo secret) {
        LocalDateTime now = LocalDateTime.now();
        secret.setGmtModified(now);

        // 加密密钥值
        String encryptedValue = encryptValue(secret.getEncryptedValue(), secret.getEncryptAlgorithm());
        secret.setEncryptedValue(encryptedValue);

        // 计算 MD5
        String md5 = calculateMd5(secret.getEncryptedValue());
        secret.setValueMd5(md5);

        // 更新版本号
        String currentVersion = secret.getKeyVersion();
        if (currentVersion != null && currentVersion.startsWith("v")) {
            int versionNum = Integer.parseInt(currentVersion.substring(1));
            secret.setKeyVersion("v" + (versionNum + 1));
        } else {
            secret.setKeyVersion("v2");
        }

        secretInfoMapper.updateById(secret);

        // 保存历史记录
        saveHistory(secret, "UPDATE");

        return secret;
    }

    @Override
    public boolean deleteSecret(String secretKey, String group, String namespace) {
        LambdaQueryWrapper<ZMistSecretInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ZMistSecretInfo::getSecretKey, secretKey)
               .eq(ZMistSecretInfo::getGroup, group)
               .eq(ZMistSecretInfo::getNamespace, namespace);

        ZMistSecretInfo secret = secretInfoMapper.selectOne(wrapper);
        if (secret != null) {
            // 保存历史记录
            saveHistory(secret, "DELETE");
            return secretInfoMapper.delete(wrapper) > 0;
        }
        return false;
    }

    @Override
    public ZMistSecretInfo getSecret(String secretKey, String group, String namespace) {
        LambdaQueryWrapper<ZMistSecretInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ZMistSecretInfo::getSecretKey, secretKey)
               .eq(ZMistSecretInfo::getGroup, group)
               .eq(ZMistSecretInfo::getNamespace, namespace);

        return secretInfoMapper.selectOne(wrapper);
    }

    @Override
    public List<ZMistSecretInfo> listSecrets(String group, String appName, String namespace) {
        LambdaQueryWrapper<ZMistSecretInfo> wrapper = new LambdaQueryWrapper<>();
        if (group != null) {
            wrapper.eq(ZMistSecretInfo::getGroup, group);
        }
        if (appName != null) {
            wrapper.eq(ZMistSecretInfo::getAppName, appName);
        }
        if (namespace != null) {
            wrapper.eq(ZMistSecretInfo::getNamespace, namespace);
        }

        return secretInfoMapper.selectList(wrapper);
    }

    @Override
    public String encryptValue(String plainValue, String algorithm) {
        try {
            if (Constance.EncryptAlgorithm.AES.equals(algorithm)) {
                return encryptAES(plainValue);
            } else if (Constance.EncryptAlgorithm.RSA.equals(algorithm)) {
                return encryptRSA(plainValue);
            }
            // 默认 AES
            return encryptAES(plainValue);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public String decryptValue(String encryptedValue, String algorithm) {
        try {
            if (Constance.EncryptAlgorithm.AES.equals(algorithm)) {
                return decryptAES(encryptedValue);
            } else if (Constance.EncryptAlgorithm.RSA.equals(algorithm)) {
                return decryptRSA(encryptedValue);
            }
            // 默认 AES
            return decryptAES(encryptedValue);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    private String encryptAES(String plainValue) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getMasterKeyBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plainValue.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decryptAES(String encryptedValue) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedValue);
        SecretKeySpec keySpec = new SecretKeySpec(getMasterKeyBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private String encryptRSA(String plainValue) throws Exception {
        // 简化版 RSA 加密，实际生产中需要使用公钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainValue.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decryptRSA(String encryptedValue) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedValue);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private byte[] getMasterKeyBytes() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(DEFAULT_MASTER_KEY.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return DEFAULT_MASTER_KEY.getBytes(StandardCharsets.UTF_8);
        }
    }

    private String calculateMd5(String value) {
        return DigestUtils.md5DigestAsHex(value.getBytes(StandardCharsets.UTF_8));
    }

    private void saveHistory(ZMistSecretInfo secret, String opType) {
        ZMistSecretHistory history = new ZMistSecretHistory();
        history.setNid(secret.getId());
        history.setSecretKey(secret.getSecretKey());
        history.setGroup(secret.getGroup());
        history.setAppName(secret.getAppName());
        history.setEncryptedValue(secret.getEncryptedValue());
        history.setValueMd5(secret.getValueMd5());
        history.setKeyVersion(secret.getKeyVersion());
        history.setOpType(opType);
        history.setNamespace(secret.getNamespace());
        LocalDateTime now = LocalDateTime.now();
        history.setGmtCreate(now);
        history.setGmtModified(now);
        secretHistoryMapper.insert(history);
    }
}