package com.zifang.ops.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ops.core.domain.entity.ImageBuildDO;
import com.zifang.ops.core.domain.service.IImageBuildService;
import com.zifang.ops.web.api.request.ImageBuildReq;
import com.zifang.ops.web.api.response.ImageBuildResp;
import com.zifang.util.core.meta.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "镜像构建")
@RestController
@RequestMapping("/api/image-build")
public class ImageBuildController {

    @Resource
    private IImageBuildService imageBuildService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public Result<List<ImageBuildResp>> list() {
        List<ImageBuildResp> data = imageBuildService.listBuild().stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public Result<IPage<ImageBuildResp>> page(@RequestBody ImageBuildReq req) {
        IPage<ImageBuildDO> page = imageBuildService.pageBuild(
                req.getPageNum() != null ? req.getPageNum() : 1,
                req.getPageSize() != null ? req.getPageSize() : 10,
                req.getImageName(), req.getAppName(), req.getBranch(), req.getEnv(), req.getStatus()
        );
        return Result.success(page.convert(this::toResp));
    }

    @Operation(summary = "详情")
    @GetMapping("/get")
    public Result<ImageBuildResp> get(@RequestParam Long id) {
        ImageBuildDO build = imageBuildService.getBuild(id);
        return Result.success(toResp(build));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Void> add(@RequestBody ImageBuildReq req) {
        ImageBuildDO build = new ImageBuildDO();
        BeanUtils.copyProperties(req, build);
        imageBuildService.createBuild(build);
        return Result.success();
    }

    private ImageBuildResp toResp(ImageBuildDO d) {
        if (d == null) return null;
        ImageBuildResp r = new ImageBuildResp();
        BeanUtils.copyProperties(d, r);
        return r;
    }
}
