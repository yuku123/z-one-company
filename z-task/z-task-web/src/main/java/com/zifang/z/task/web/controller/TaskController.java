package com.zifang.z.task.web.controller;

import com.zifang.util.core.meta.Result;
import com.zifang.z.task.core.entity.Task;
import com.zifang.z.task.core.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 任务表 Controller
 *
 * @author zifang
 */
@Tag(name = "任务管理")
@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @Operation(summary = "任务列表（按列表）")
    @GetMapping("/list")
    public Result<List<Task>> listByList(@RequestParam Long listId) {
        return Result.success(taskService.getTasksByList(listId));
    }

    @Operation(summary = "任务列表（按项目）")
    @GetMapping("/project/{projectId}")
    public Result<List<Task>> listByProject(@PathVariable Long projectId) {
        return Result.success(taskService.getTasksByProject(projectId));
    }

    @Operation(summary = "任务列表（按负责人）")
    @GetMapping("/assignee/{userId}")
    public Result<List<Task>> listByAssignee(@PathVariable String userId) {
        return Result.success(taskService.getTasksByAssignee(userId));
    }

    @Operation(summary = "创建任务")
    @PostMapping
    public Result<Task> create(@RequestBody Task task, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return Result.success(taskService.createTask(task, userId));
    }

    @Operation(summary = "移动任务")
    @PutMapping("/move")
    public Result<Boolean> move(@RequestParam Long taskId,
                                @RequestParam Long targetListId,
                                @RequestParam String position,
                                @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return Result.success(taskService.moveTask(taskId, targetListId, position, userId));
    }

    @Operation(summary = "添加执行者")
    @PostMapping("/{taskId}/assignees")
    public Result<Boolean> addAssignee(@PathVariable Long taskId,
                                       @RequestParam String userId,
                                       @RequestHeader(value = "X-User-Id", required = false) String operatorId) {
        return Result.success(taskService.addAssignee(taskId, userId, operatorId));
    }

    @Operation(summary = "移除执行者")
    @DeleteMapping("/{taskId}/assignees/{userId}")
    public Result<Boolean> removeAssignee(@PathVariable Long taskId,
                                         @PathVariable String userId,
                                         @RequestHeader(value = "X-User-Id", required = false) String operatorId) {
        return Result.success(taskService.removeAssignee(taskId, userId, operatorId));
    }

    @Operation(summary = "完成任务")
    @PostMapping("/{taskId}/complete")
    public Result<Boolean> complete(@PathVariable Long taskId,
                                    @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return Result.success(taskService.completeTask(taskId, userId));
    }

    @Operation(summary = "重新打开任务")
    @PostMapping("/{taskId}/reopen")
    public Result<Boolean> reopen(@PathVariable Long taskId,
                                  @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return Result.success(taskService.reopenTask(taskId, userId));
    }
}
