package com.zifang.z.task.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.task.core.service.ProjectBizService;
import com.zifang.z.task.core.service.dto.ProjectDTO;
import com.zifang.z.task.web.api.request.ProjectReq;
import com.zifang.z.task.web.api.response.ProjectResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "项目管理")
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Resource
    private ProjectBizService projectBizService;

    @Operation(summary = "获取用户项目列表")
    @GetMapping("/user/list")
    public Result<List<ProjectResp>> listByUser(@RequestParam String userId) {
        List<ProjectResp> data = projectBizService.listByUser(userId).stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "创建项目")
    @PostMapping
    public Result<ProjectResp> create(@RequestBody ProjectReq req,
                                       @RequestHeader(value = "X-User-Id", required = false) String userId) {
        ProjectDTO dto = new ProjectDTO();
        BeanUtils.copyProperties(req, dto);
        return Result.success(toResp(projectBizService.create(dto, userId)));
    }

    @Operation(summary = "归档项目")
    @PutMapping("/{projectId}/archive")
    public Result<Boolean> archive(@PathVariable Long projectId,
                                    @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return Result.success(projectBizService.archive(projectId, userId));
    }

    private ProjectResp toResp(ProjectDTO dto) {
        if (dto == null) return null;
        ProjectResp resp = new ProjectResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }
}
