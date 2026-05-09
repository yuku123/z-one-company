package com.zifang.ops.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ops.core.domain.entity.ImageDO;
import com.zifang.ops.core.domain.entity.ImageTagDO;
import com.zifang.ops.core.domain.service.IImageService;
import com.zifang.ops.web.api.request.ImageReq;
import com.zifang.ops.web.api.request.ImageTagReq;
import com.zifang.ops.web.api.response.ImageResp;
import com.zifang.ops.web.api.response.ImageTagResp;
import com.zifang.util.core.meta.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "镜像管理")
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    private IImageService imageService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public Result<List<ImageResp>> list() {
        List<ImageResp> data = imageService.listImage().stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<IPage<ImageResp>> page(@RequestBody ImageReq req) {
        IPage<ImageDO> page = imageService.pageImage(
                req.getPageNum() != null ? req.getPageNum() : 1,
                req.getPageSize() != null ? req.getPageSize() : 10,
                req.getName()
        );
        return Result.success(page.convert(this::toResp));
    }

    @Operation(summary = "详情")
    @GetMapping("/get")
    public Result<ImageResp> get(@RequestParam Long id) {
        ImageDO image = imageService.getImage(id);
        return Result.success(toResp(image));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody ImageReq req) {
        ImageDO image = new ImageDO();
        BeanUtils.copyProperties(req, image);
        imageService.createImage(image);
        return Result.success();
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody ImageReq req) {
        ImageDO image = new ImageDO();
        BeanUtils.copyProperties(req, image);
        imageService.updateImage(image);
        return Result.success();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        imageService.deleteImage(id);
        return Result.success();
    }

    @Operation(summary = "获取镜像版本列表")
    @GetMapping("/tags")
    public Result<List<ImageTagResp>> tags(@RequestParam Long imageId) {
        List<ImageTagResp> data = imageService.getTags(imageId).stream().map(this::toTagResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "添加版本")
    @PostMapping("/tag")
    public Result<Void> addTag(@RequestBody ImageTagReq req) {
        ImageTagDO tag = new ImageTagDO();
        BeanUtils.copyProperties(req, tag);
        imageService.addTag(tag);
        return Result.success();
    }

    @Operation(summary = "删除版本")
    @PostMapping("/tag/delete")
    public Result<Void> deleteTag(@RequestParam Long id) {
        imageService.deleteTag(id);
        return Result.success();
    }

    private ImageResp toResp(ImageDO d) {
        if (d == null) return null;
        ImageResp r = new ImageResp();
        BeanUtils.copyProperties(d, r);
        return r;
    }

    private ImageTagResp toTagResp(ImageTagDO d) {
        if (d == null) return null;
        ImageTagResp r = new ImageTagResp();
        BeanUtils.copyProperties(d, r);
        return r;
    }
}
