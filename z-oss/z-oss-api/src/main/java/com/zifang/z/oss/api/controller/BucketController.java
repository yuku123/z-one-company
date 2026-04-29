package com.zifang.z.oss.api.controller;

import com.zifang.z.oss.api.config.AuthenticationInterceptor;
import com.zifang.z.oss.api.dto.CreateBucketRequest;
import com.zifang.z.oss.api.dto.UpdateBucketRequest;
import com.zifang.z.oss.api.vo.BucketVO;
import com.zifang.z.oss.core.domain.entity.OssBucket;
import com.zifang.z.oss.core.domain.entity.OssUser;
import com.zifang.z.oss.core.domain.service.IOssBucketService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存储桶API控制器
 */

@RestController
@RequestMapping("/api/v1/bucket")
public class BucketController {

    @Autowired
    private IOssBucketService bucketService;

    
    @PostMapping
    public ResponseEntity<BucketVO> createBucket(@RequestBody CreateBucketRequest request,
                                                  HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.createBucket(request.getName(), user.getId());
        return ResponseEntity.ok(convertBucket(bucket));
    }

    
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<Void> deleteBucket(@PathVariable String bucketName,
                                              HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        bucketService.deleteBucket(bucketName, user.getId());
        return ResponseEntity.noContent().build();
    }

    
    @GetMapping
    public ResponseEntity<List<BucketVO>> listBuckets(HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        List<OssBucket> buckets = bucketService.listUserBuckets(user.getId());
        return ResponseEntity.ok(buckets.stream().map(this::convertBucket).collect(Collectors.toList()));
    }

    
    @GetMapping("/{bucketName}")
    public ResponseEntity<BucketVO> getBucket(@PathVariable String bucketName,
                                               HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.validateBucket(bucketName, user.getId());
        return ResponseEntity.ok(convertBucket(bucket));
    }

    
    @PutMapping("/{bucketName}")
    public ResponseEntity<BucketVO> updateBucket(@PathVariable String bucketName,
                                                  @RequestBody UpdateBucketRequest request,
                                                  HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.updateBucket(bucketName, user.getId(), request.getAcl(),
                request.getRegion(), request.getPolicy());
        return ResponseEntity.ok(convertBucket(bucket));
    }

    
    @GetMapping("/{bucketName}/acl")
    public ResponseEntity<Map<String, String>> getBucketAcl(@PathVariable String bucketName,
                                                              HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.validateBucket(bucketName, user.getId());
        Map<String, String> acl = new HashMap<>();
        acl.put("acl", bucket.getAcl());
        return ResponseEntity.ok(acl);
    }

    
    @PutMapping("/{bucketName}/acl")
    public ResponseEntity<BucketVO> setBucketAcl(@PathVariable String bucketName,
                                                  @RequestBody Map<String, String> aclMap,
                                                  HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.updateBucket(bucketName, user.getId(), aclMap.get("acl"),
                null, null);
        return ResponseEntity.ok(convertBucket(bucket));
    }

    
    @GetMapping("/{bucketName}/policy")
    public ResponseEntity<Map<String, Object>> getBucketPolicy(@PathVariable String bucketName,
                                                                 HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.validateBucket(bucketName, user.getId());
        Map<String, Object> policy = new HashMap<>();
        policy.put("policy", bucket.getPolicy());
        return ResponseEntity.ok(policy);
    }

    
    @PutMapping("/{bucketName}/policy")
    public ResponseEntity<BucketVO> setBucketPolicy(@PathVariable String bucketName,
                                                     @RequestBody Map<String, String> policyMap,
                                                     HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssBucket bucket = bucketService.updateBucket(bucketName, user.getId(), null,
                null, policyMap.get("policy"));
        return ResponseEntity.ok(convertBucket(bucket));
    }

    
    @DeleteMapping("/{bucketName}/policy")
    public ResponseEntity<Void> deleteBucketPolicy(@PathVariable String bucketName,
                                                    HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        bucketService.updateBucket(bucketName, user.getId(), null, null, null);
        return ResponseEntity.noContent().build();
    }

    
    @RequestMapping(value = "/{bucketName}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> headBucket(@PathVariable String bucketName,
                                            HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        bucketService.validateBucket(bucketName, user.getId());
        return ResponseEntity.ok().build();
    }

    private BucketVO convertBucket(OssBucket bucket) {
        BucketVO vo = new BucketVO();
        vo.setId(bucket.getId());
        vo.setName(bucket.getName());
        vo.setRegion(bucket.getRegion());
        vo.setAcl(bucket.getAcl());
        vo.setCreateTime(bucket.getCreateTime());
        return vo;
    }
}