package com.zifang.z.mist.core.domain.service;

import com.zifang.z.mist.core.domain.entity.ZMistSecretInfo;
import java.util.List;

/**
 * 密钥服务接口
 */
public interface IZMistSecretService {

    /**
     * 保存密钥
     */
    ZMistSecretInfo saveSecret(ZMistSecretInfo secret);

    /**
     * 更新密钥
     */
    ZMistSecretInfo updateSecret(ZMistSecretInfo secret);

    /**
     * 删除密钥
     */
    boolean deleteSecret(String secretKey, String group, String namespace);

    /**
     * 根据 key 查询密钥
     */
    ZMistSecretInfo getSecret(String secretKey, String group, String namespace);

    /**
     * 查询密钥列表
     */
    List<ZMistSecretInfo> listSecrets(String group, String appName, String namespace);

    /**
     * 加密密钥值
     */
    String encryptValue(String plainValue, String algorithm);

    /**
     * 解密密钥值
     */
    String decryptValue(String encryptedValue, String algorithm);
}