package com.zifang.z.oss.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.oss.common.enums.BucketAcl;
import com.zifang.z.oss.common.exception.BucketException;
import com.zifang.z.oss.core.domain.entity.OssBucket;
import com.zifang.z.oss.core.domain.mapper.OssBucketMapper;
import com.zifang.z.oss.core.domain.service.IOssBucketService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 桶服务实现
 */
@Service
public class OssBucketServiceImpl extends ServiceImpl<OssBucketMapper, OssBucket> implements IOssBucketService {

    @Override
    public OssBucket createBucket(String name, Long userId) {
        // 验证桶名格式
        validateBucketName(name);

        // 检查桶是否已存在
        OssBucket existBucket = this.getOne(new LambdaQueryWrapper<OssBucket>()
                .eq(OssBucket::getName, name)
                .eq(OssBucket::getUserId, userId));
        if (existBucket != null) {
            throw new BucketException("Bucket already exists: " + name);
        }

        OssBucket bucket = new OssBucket();
        bucket.setName(name);
        bucket.setUserId(userId);
        bucket.setRegion("default");
        bucket.setAcl(BucketAcl.PRIVATE.getCode());
        this.save(bucket);

        return bucket;
    }

    @Override
    public void deleteBucket(String name, Long userId) {
        OssBucket bucket = validateBucket(name, userId);
        this.removeById(bucket.getId());
    }

    @Override
    public List<OssBucket> listUserBuckets(Long userId) {
        return this.list(new LambdaQueryWrapper<OssBucket>()
                .eq(OssBucket::getUserId, userId));
    }

    @Override
    public OssBucket validateBucket(String name, Long userId) {
        OssBucket bucket = this.getOne(new LambdaQueryWrapper<OssBucket>()
                .eq(OssBucket::getName, name)
                .eq(OssBucket::getUserId, userId));
        if (bucket == null) {
            throw new BucketException("Bucket not found: " + name);
        }
        return bucket;
    }

    @Override
    public OssBucket updateBucket(String name, Long userId, String acl, String region, String policy) {
        OssBucket bucket = validateBucket(name, userId);

        if (acl != null) {
            bucket.setAcl(acl);
        }
        if (region != null) {
            bucket.setRegion(region);
        }
        if (policy != null) {
            bucket.setPolicy(policy);
        }

        this.updateById(bucket);
        return bucket;
    }

    @Override
    public Map<String, Object> getBucketStats(String name, Long userId) {
        OssBucket bucket = validateBucket(name, userId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("bucketName", bucket.getName());
        stats.put("region", bucket.getRegion());
        stats.put("acl", bucket.getAcl());
        stats.put("createTime", bucket.getCreateTime());
        return stats;
    }

    private void validateBucketName(String name) {
        // 桶名只能包含小写字母、数字、连字符
        if (!name.matches("^[a-z0-9][a-z0-9-]{2,62}[a-z0-9]$")) {
            throw new BucketException("Invalid bucket name: " + name);
        }
    }
}