package com.zifang.z.oss.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.oss.common.exception.ObjectException;
import com.zifang.z.oss.core.domain.entity.OssBucket;
import com.zifang.z.oss.core.domain.entity.OssObject;
import com.zifang.z.oss.core.domain.mapper.OssObjectMapper;
import com.zifang.z.oss.core.domain.service.IOssBucketService;
import com.zifang.z.oss.core.domain.service.IOssObjectService;
import com.zifang.z.oss.core.storage.StorageEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 对象服务实现
 */
@Service
public class OssObjectServiceImpl extends ServiceImpl<OssObjectMapper, OssObject> implements IOssObjectService {

    private final IOssBucketService bucketService;
    private final StorageEngine storageEngine;

    @Value("${server.port:8088}")
    private int serverPort;

    @Value("${oss.storage.file.root:/data/oss}")
    private String storageRoot;

    public OssObjectServiceImpl(IOssBucketService bucketService, StorageEngine storageEngine) {
        this.bucketService = bucketService;
        this.storageEngine = storageEngine;
    }

    @Override
    public OssObject uploadObject(String bucketName, String objectKey, InputStream inputStream,
                                   long size, String contentType, Long userId) {
        // 验证桶
        OssBucket bucket = bucketService.validateBucket(bucketName, userId);

        // 存储到文件系统
        String etag = storageEngine.store(bucketName, objectKey, inputStream, size);

        // 保存元数据
        OssObject object = new OssObject();
        object.setBucketId(bucket.getId());
        object.setBucketName(bucketName);
        object.setObjectKey(objectKey);
        object.setObjectName(getObjectName(objectKey));
        object.setContentType(contentType);
        object.setContentLength(size);
        object.setEtag(etag);
        object.setStoragePath(bucketName + "/" + objectKey);
        object.setUserId(userId);
        object.setIsFolder(0);
        this.save(object);

        return object;
    }

    @Override
    public InputStream downloadObject(String bucketName, String objectKey, Long userId) {
        // 验证桶
        bucketService.validateBucket(bucketName, userId);

        // 验证对象存在
        OssObject object = getObject(bucketName, objectKey, userId);
        if (object == null) {
            throw new ObjectException("Object not found: " + objectKey);
        }

        return storageEngine.read(bucketName, objectKey);
    }

    @Override
    public void deleteObject(String bucketName, String objectKey, Long userId) {
        // 验证桶
        bucketService.validateBucket(bucketName, userId);

        // 获取对象
        OssObject object = getObject(bucketName, objectKey, userId);
        if (object != null) {
            // 删除物理文件
            storageEngine.delete(bucketName, objectKey);
            // 删除元数据
            this.removeById(object.getId());
        }
    }

    @Override
    public OssObject getObject(String bucketName, String objectKey, Long userId) {
        return this.getOne(new LambdaQueryWrapper<OssObject>()
                .eq(OssObject::getBucketName, bucketName)
                .eq(OssObject::getObjectKey, objectKey)
                .eq(OssObject::getUserId, userId));
    }

    @Override
    public List<OssObject> listObjects(String bucketName, String prefix, Long userId) {
        // 验证桶
        bucketService.validateBucket(bucketName, userId);

        LambdaQueryWrapper<OssObject> wrapper = new LambdaQueryWrapper<OssObject>()
                .eq(OssObject::getBucketName, bucketName)
                .eq(OssObject::getUserId, userId);

        if (prefix != null && !prefix.isEmpty()) {
            wrapper.likeRight(OssObject::getObjectKey, prefix);
        }

        wrapper.orderByAsc(OssObject::getObjectKey);
        return this.list(wrapper);
    }

    @Override
    public OssObject createFolder(String bucketName, String folderKey, Long userId) {
        // 验证桶
        OssBucket bucket = bucketService.validateBucket(bucketName, userId);

        // 确保folderKey以/结尾
        if (!folderKey.endsWith("/")) {
            folderKey = folderKey + "/";
        }

        // 检查是否已存在
        OssObject existObject = getObject(bucketName, folderKey, userId);
        if (existObject != null) {
            return existObject;
        }

        // 创建文件夹元数据
        OssObject object = new OssObject();
        object.setBucketId(bucket.getId());
        object.setBucketName(bucketName);
        object.setObjectKey(folderKey);
        object.setObjectName(folderKey);
        object.setContentType("application/directory");
        object.setContentLength(0L);
        object.setEtag("");
        object.setStoragePath(bucketName + "/" + folderKey);
        object.setUserId(userId);
        object.setIsFolder(1);
        this.save(object);

        return object;
    }

    @Override
    public OssObject copyObject(String sourceBucketName, String sourceObjectKey,
                                 String destBucketName, String destObjectKey, Long userId) {
        // 验证源桶和目标桶
        bucketService.validateBucket(sourceBucketName, userId);
        bucketService.validateBucket(destBucketName, userId);

        // 获取源对象
        OssObject sourceObject = getObject(sourceBucketName, sourceObjectKey, userId);
        if (sourceObject == null) {
            throw new ObjectException("Source object not found: " + sourceObjectKey);
        }

        // 读取源文件内容
        InputStream inputStream = storageEngine.read(sourceBucketName, sourceObjectKey);

        // 存储到目标位置
        String etag = storageEngine.store(destBucketName, destObjectKey, inputStream, sourceObject.getContentLength());

        // 创建目标对象元数据
        OssBucket destBucket = bucketService.validateBucket(destBucketName, userId);
        OssObject destObject = new OssObject();
        destObject.setBucketId(destBucket.getId());
        destObject.setBucketName(destBucketName);
        destObject.setObjectKey(destObjectKey);
        destObject.setObjectName(getObjectName(destObjectKey));
        destObject.setContentType(sourceObject.getContentType());
        destObject.setContentLength(sourceObject.getContentLength());
        destObject.setEtag(etag);
        destObject.setStoragePath(destBucketName + "/" + destObjectKey);
        destObject.setUserId(userId);
        destObject.setIsFolder(0);
        this.save(destObject);

        return destObject;
    }

    @Override
    public void batchDeleteObjects(String bucketName, List<String> objectKeys, Long userId) {
        // 验证桶
        bucketService.validateBucket(bucketName, userId);

        for (String objectKey : objectKeys) {
            deleteObject(bucketName, objectKey, userId);
        }
    }

    @Override
    public String generatePresignedUrl(String bucketName, String objectKey, int expires, Long userId) {
        // 验证桶和对象
        bucketService.validateBucket(bucketName, userId);
        OssObject object = getObject(bucketName, objectKey, userId);
        if (object == null) {
            throw new ObjectException("Object not found: " + objectKey);
        }

        // 生成简单的预签名URL（实际生产中应该使用更复杂的签名算法）
        String token = UUID.randomUUID().toString().replace("-", "");
        String expiryTime = String.valueOf(System.currentTimeMillis() + expires * 1000);

        return String.format("http://localhost:%d/api/v1/object/%s/%s?token=%s&expires=%s",
                serverPort, bucketName, objectKey, token, expiryTime);
    }

    @Override
    public Map<String, Object> getBucketStats(String bucketName, Long userId) {
        // 验证桶
        bucketService.validateBucket(bucketName, userId);

        // 统计对象数量
        long objectCount = this.count(new LambdaQueryWrapper<OssObject>()
                .eq(OssObject::getBucketName, bucketName)
                .eq(OssObject::getUserId, userId)
                .eq(OssObject::getIsFolder, Integer.valueOf(0)));

        // 统计总大小
        List<OssObject> objects = this.list(new LambdaQueryWrapper<OssObject>()
                .eq(OssObject::getBucketName, bucketName)
                .eq(OssObject::getUserId, userId)
                .eq(OssObject::getIsFolder, (Integer)0));

        long totalSize = 0;
        for (OssObject obj : objects) {
            if (obj.getContentLength() != null) {
                totalSize += obj.getContentLength();
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("bucketName", bucketName);
        stats.put("objectCount", objectCount);
        stats.put("totalSize", totalSize);
        return stats;
    }

    private String getObjectName(String objectKey) {
        int lastSlash = objectKey.lastIndexOf('/');
        return lastSlash >= 0 ? objectKey.substring(lastSlash + 1) : objectKey;
    }
}