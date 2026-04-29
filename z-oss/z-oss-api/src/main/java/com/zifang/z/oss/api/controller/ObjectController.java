package com.zifang.z.oss.api.controller;

import com.zifang.z.oss.api.config.AuthenticationInterceptor;
import com.zifang.z.oss.api.vo.ObjectVO;
import com.zifang.z.oss.core.domain.entity.OssObject;
import com.zifang.z.oss.core.domain.entity.OssUser;
import com.zifang.z.oss.core.domain.service.IOssObjectService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对象存储API控制器
 */

@RestController
@RequestMapping("/api/v1")
public class ObjectController {

    @Autowired
    private IOssObjectService objectService;

    // ==================== 对象操作 ====================

    @PostMapping("/object/{bucketName}/{objectKey:.+}")
    public ResponseEntity<ObjectVO> uploadObject(@PathVariable String bucketName,
                                                  @PathVariable String objectKey,
                                                  @RequestParam("file") MultipartFile file,
                                                  HttpServletRequest httpRequest) throws IOException {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);

        InputStream inputStream = file.getInputStream();
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        OssObject object = objectService.uploadObject(
                bucketName, objectKey, inputStream, file.getSize(), contentType, user.getId());

        return ResponseEntity.ok(convertObject(object));
    }

    @GetMapping("/object/{bucketName}/{objectKey:.+}")
    public ResponseEntity<byte[]> downloadObject(@PathVariable String bucketName,
                                                  @PathVariable String objectKey,
                                                  HttpServletRequest httpRequest) throws IOException {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);

        InputStream inputStream = objectService.downloadObject(bucketName, objectKey, user.getId());
        OssObject objectMeta = objectService.getObject(bucketName, objectKey, user.getId());

        byte[] data = toByteArray(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(objectMeta.getContentType()));
        headers.setContentLength(objectMeta.getContentLength());
        headers.setETag("\"" + objectMeta.getEtag() + "\"");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024 * 8];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    @DeleteMapping("/object/{bucketName}/{objectKey:.+}")
    public ResponseEntity<Void> deleteObject(@PathVariable String bucketName,
                                              @PathVariable String objectKey,
                                              HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        objectService.deleteObject(bucketName, objectKey, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/object/{bucketName}")
    public ResponseEntity<List<ObjectVO>> listObjects(@PathVariable String bucketName,
                                                       @RequestParam(value = "prefix", required = false) String prefix,
                                                       HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        List<OssObject> objects = objectService.listObjects(bucketName, prefix, user.getId());
        return ResponseEntity.ok(objects.stream().map(this::convertObject).collect(Collectors.toList()));
    }

    @PostMapping("/folder/{bucketName}/{folderKey:.+}")
    public ResponseEntity<ObjectVO> createFolder(@PathVariable String bucketName,
                                                  @PathVariable String folderKey,
                                                  HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssObject object = objectService.createFolder(bucketName, folderKey, user.getId());
        return ResponseEntity.ok(convertObject(object));
    }

    // ==================== 扩展对象操作 ====================

    
    @RequestMapping(value = "/object/{bucketName}/{objectKey:.+}", method = RequestMethod.HEAD)
    public ResponseEntity<Map<String, Object>> headObject(@PathVariable String bucketName,
                                                           @PathVariable String objectKey,
                                                           HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        OssObject object = objectService.getObject(bucketName, objectKey, user.getId());
        if (object == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", object.getObjectName());
        metadata.put("key", object.getObjectKey());
        metadata.put("size", object.getContentLength());
        metadata.put("contentType", object.getContentType());
        metadata.put("etag", object.getEtag());
        metadata.put("lastModified", object.getUpdateTime());
        metadata.put("isFolder", object.getIsFolder() == 1);

        return ResponseEntity.ok(metadata);
    }

    
    @PostMapping("/object/{bucketName}/{objectKey:.+}/copy")
    public ResponseEntity<ObjectVO> copyObject(@PathVariable String bucketName,
                                                @PathVariable String objectKey,
                                                @RequestBody Map<String, String> copyRequest,
                                                HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        String destBucketName = copyRequest.get("destBucketName");
        String destObjectKey = copyRequest.get("destObjectKey");

        OssObject object = objectService.copyObject(bucketName, objectKey, destBucketName, destObjectKey, user.getId());
        return ResponseEntity.ok(convertObject(object));
    }

    
    @PostMapping("/object/{bucketName}/batch-delete")
    public ResponseEntity<Void> batchDeleteObjects(@PathVariable String bucketName,
                                                    @RequestBody List<String> objectKeys,
                                                    HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        objectService.batchDeleteObjects(bucketName, objectKeys, user.getId());
        return ResponseEntity.noContent().build();
    }

    
    @GetMapping("/object/{bucketName}/{objectKey:.+}/url")
    public ResponseEntity<Map<String, String>> getObjectUrl(@PathVariable String bucketName,
                                                              @PathVariable String objectKey,
                                                              @RequestParam(value = "expires", defaultValue = "3600") Integer expires,
                                                              HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        String url = objectService.generatePresignedUrl(bucketName, objectKey, expires, user.getId());
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return ResponseEntity.ok(result);
    }

    
    @GetMapping("/bucket/{bucketName}/stats")
    public ResponseEntity<Map<String, Object>> getBucketStats(@PathVariable String bucketName,
                                                                HttpServletRequest httpRequest) {
        OssUser user = (OssUser) httpRequest.getAttribute(AuthenticationInterceptor.ATTR_USER);
        Map<String, Object> stats = objectService.getBucketStats(bucketName, user.getId());
        return ResponseEntity.ok(stats);
    }

    // ==================== 转换方法 ====================

    private ObjectVO convertObject(OssObject object) {
        ObjectVO vo = new ObjectVO();
        vo.setKey(object.getObjectKey());
        vo.setName(object.getObjectName());
        vo.setSize(object.getContentLength());
        vo.setEtag(object.getEtag());
        vo.setContentType(object.getContentType());
        vo.setLastModified(object.getUpdateTime());
        vo.setFolder(object.getIsFolder() == 1);
        return vo;
    }
}