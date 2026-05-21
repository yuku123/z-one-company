package com.zifang.z.wf.starter.api;


import com.zifang.util.core.meta.Result;
import com.zifang.z.wf.starter.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批中心Controller
 * 提供给前端的统一审批中心API
 */
@RestController
@RequestMapping("/approval-center")
@Tag(name = "001_审批中心")

public class ApprovalCenterController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ==================== 1. 仪表盘统计 ====================

    @GetMapping("/dashboard")
    @Operation(summary = "001_获取仪表盘统计数据")
    
    public Result<DashboardStatsVO> getDashboardStats(
            @Parameter(description = "当前用户ID") @RequestParam String userId) {

        DashboardStatsVO stats = new DashboardStatsVO();

        // 待我审批数量
        long todoCount = taskService.createTaskQuery()
                .taskAssignee(userId)
                .active()
                .count();
        stats.setTodoCount(todoCount);

        // 我已审批数量（历史任务）
        long doneCount = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .count();
        stats.setDoneCount(doneCount);

        // 我发起的流程数量
        long myProcessCount = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId)
                .count();
        stats.setMyProcessCount(myProcessCount);

        // 抄送我的（需要根据业务实现，暂时设为0）
        stats.setCcCount(0L);

        return Result.success(stats);
    }

    // ==================== 2. 待办任务 ====================

    @GetMapping("/tasks/todo")
    @Operation(summary = "002_获取待办任务列表")
    
    public Result<PageResult<TaskSummaryVO>> getTodoTasks(
            @Parameter(description = "当前用户ID") @RequestParam String userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        // 获取总数
        long total = taskService.createTaskQuery()
                .taskAssignee(userId)
                .active()
                .count();

        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .listPage((pageNum - 1) * pageSize, pageSize);

        List<TaskSummaryVO> result = tasks.stream().map(task -> {
            TaskSummaryVO vo = new TaskSummaryVO();
            vo.setTaskId(task.getId());
            vo.setTaskName(task.getName());
            vo.setProcessInstanceId(task.getProcessInstanceId());
            vo.setAssignee(task.getAssignee());
            // Task接口没有getCreateTime()，需要从HistoricTaskInstance获取
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                    .taskId(task.getId())
                    .singleResult();
            if (historicTask != null && historicTask.getStartTime() != null) {
                vo.setCreateTime(sdf.format(historicTask.getStartTime()));
            }
            vo.setPriority(task.getPriority());
            vo.setStatus("todo");

            // 获取流程定义名称
            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (historicInstance != null) {
                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(historicInstance.getProcessDefinitionId())
                        .singleResult();
                vo.setProcessName(pd != null ? pd.getName() : "");
                vo.setInitiator(historicInstance.getStartUserId());
            }

            return vo;
        }).collect(Collectors.toList());

        PageResult<TaskSummaryVO> pageResult = PageResult.of(result, total, pageNum, pageSize);
        return Result.success(pageResult);
    }

    // ==================== 3. 已办任务 ====================

    @GetMapping("/tasks/done")
    @Operation(summary = "003_获取已办任务列表")
    
    public Result<PageResult<TaskSummaryVO>> getDoneTasks(
            @Parameter(description = "当前用户ID") @RequestParam String userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        // 获取总数
        long total = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .count();

        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .listPage((pageNum - 1) * pageSize, pageSize);

        List<TaskSummaryVO> result = tasks.stream().map(task -> {
            TaskSummaryVO vo = new TaskSummaryVO();
            vo.setTaskId(task.getId());
            vo.setTaskName(task.getName());
            vo.setProcessInstanceId(task.getProcessInstanceId());
            vo.setAssignee(task.getAssignee());
            // HistoricTaskInstance 有 getCreateTime() 方法
            if (task.getStartTime() != null) {
                vo.setCreateTime(sdf.format(task.getStartTime()));
            }
            vo.setPriority(task.getPriority());
            vo.setStatus("done");

            // 获取流程定义名称
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            if (hpi != null) {
                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(hpi.getProcessDefinitionId())
                        .singleResult();
                if (pd != null) {
                    vo.setProcessName(pd.getName());
                }
                vo.setInitiator(hpi.getStartUserId());
            }

            return vo;
        }).collect(Collectors.toList());

        PageResult<TaskSummaryVO> pageResult = PageResult.of(result, total, pageNum, pageSize);
        return Result.success(pageResult);
    }

    // ==================== 4. 任务详情 ====================

    @GetMapping("/tasks/get")
    @Operation(summary = "004_获取任务详情")
    
    public Result<TaskDetailVO> getTaskDetail(
            @Parameter(description = "任务ID") @RequestParam String taskId) {

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return Result.fail("任务不存在");
        }

        TaskDetailVO vo = new TaskDetailVO();
        vo.setTaskId(task.getId());
        vo.setTaskName(task.getName());
        vo.setDescription(task.getDescription());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setAssignee(task.getAssignee());
        // 从HistoricTaskInstance获取创建时间
        HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                .taskId(task.getId())
                .singleResult();
        if (historicTask != null && historicTask.getStartTime() != null) {
            vo.setCreateTime(sdf.format(historicTask.getStartTime()));
        }
        vo.setDueDate(task.getDueDate() != null ? sdf.format(task.getDueDate()) : null);
        vo.setPriority(task.getPriority());

        // 获取流程实例信息
        HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        if (historicInstance != null) {
            vo.setInitiator(historicInstance.getStartUserId());

            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(historicInstance.getProcessDefinitionId())
                    .singleResult();
            vo.setProcessName(pd != null ? pd.getName() : "");

            // 获取流程变量/表单数据
            vo.setFormData(runtimeService.getVariables(historicInstance.getId()));
        }

        // 获取审批历史
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        List<TaskDetailVO.ApprovalHistoryVO> historyList = activities.stream()
                .filter(act -> act.getActivityType() != null && act.getActivityType().contains("Task"))
                .map(act -> {
                    TaskDetailVO.ApprovalHistoryVO history = new TaskDetailVO.ApprovalHistoryVO();
                    history.setActivityName(act.getActivityName());
                    history.setAssignee(act.getAssignee());
                    history.setStartTime(sdf.format(act.getStartTime()));
                    if (act.getEndTime() != null) {
                        history.setEndTime(sdf.format(act.getEndTime()));
                        history.setDuration(formatDuration(act.getDurationInMillis()));
                    }
                    return history;
                }).collect(Collectors.toList());

        vo.setApprovalHistory(historyList);

        return Result.success(vo);
    }

    // ==================== 5. 完成任务/审批 ====================

    @PostMapping("/tasks/complete")
    @Operation(summary = "005_完成任务审批")
    
    public Result<String> completeTask(
            @Parameter(description = "任务ID") @RequestParam String taskId,
            @RequestBody ApprovalRequestDTO request) {

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return Result.fail("任务不存在或已被处理");
        }

        // 构建流程变量
        Map<String, Object> variables = new HashMap<>();
        if (request.getApprovalResult() != null) {
            variables.put("approvalResult", request.getApprovalResult());
        }
        if (request.getComment() != null) {
            variables.put("comment", request.getComment());
        }
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }

        // 完成任务
        taskService.complete(taskId, variables);

        return Result.success("审批成功");
    }

    // ==================== 6. 我发起的流程 ====================

    @GetMapping("/my-processes")
    @Operation(summary = "006_获取我发起的流程列表")
    
    public Result<PageResult<ProcessInstanceVO>> getMyProcesses(
            @Parameter(description = "当前用户ID") @RequestParam String userId,
            @Parameter(description = "流程状态: running-运行中, completed-已完成, all-全部") @RequestParam(defaultValue = "all") String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        List<ProcessInstanceVO> result = new ArrayList<>();
        long total = 0;

        if ("running".equals(status) || "all".equals(status)) {
            // 查询运行中的流程（使用HistoricProcessInstanceQuery，因为ProcessInstanceQuery没有startedBy方法）
            HistoricProcessInstanceQuery runningQuery = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(userId)
                    .unfinished()
                    .orderByProcessInstanceStartTime()
                    .desc();

            total += runningQuery.count();

            List<HistoricProcessInstance> runningInstances = runningQuery.listPage((pageNum - 1) * pageSize, pageSize);

            for (HistoricProcessInstance historicInstance : runningInstances) {
                ProcessInstanceVO vo = new ProcessInstanceVO();
                vo.setProcessInstanceId(historicInstance.getId());
                vo.setProcessDefinitionId(historicInstance.getProcessDefinitionId());
                vo.setStatus("running");

                // 从HistoricProcessInstance获取发起人信息
                vo.setInitiator(historicInstance.getStartUserId());
                if (historicInstance.getStartTime() != null) {
                    vo.setStartTime(sdf.format(historicInstance.getStartTime()));
                }

                // 获取流程定义信息
                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(historicInstance.getProcessDefinitionId())
                        .singleResult();
                if (pd != null) {
                    vo.setProcessName(pd.getName());
                    vo.setProcessKey(pd.getKey());
                }

                // 获取当前任务
                Task currentTask = taskService.createTaskQuery()
                        .processInstanceId(historicInstance.getId())
                        .singleResult();
                if (currentTask != null) {
                    vo.setCurrentTaskName(currentTask.getName());
                    vo.setCurrentAssignee(currentTask.getAssignee());
                }

                result.add(vo);
            }
        }

        if ("completed".equals(status) || "all".equals(status)) {
            // 查询已完成的流程
            HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(userId)
                    .finished()
                    .orderByProcessInstanceEndTime()
                    .desc();

            total += query.count();

            List<HistoricProcessInstance> instances = query.listPage((pageNum - 1) * pageSize, pageSize);

            for (HistoricProcessInstance instance : instances) {
                ProcessInstanceVO vo = new ProcessInstanceVO();
                vo.setProcessInstanceId(instance.getId());
                vo.setProcessDefinitionId(instance.getProcessDefinitionId());
                vo.setInitiator(instance.getStartUserId());
                if (instance.getStartTime() != null) {
                    vo.setStartTime(sdf.format(instance.getStartTime()));
                }
                if (instance.getEndTime() != null) {
                    vo.setEndTime(sdf.format(instance.getEndTime()));
                }
                vo.setStatus("completed");

                // 获取流程定义信息
                ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(instance.getProcessDefinitionId())
                        .singleResult();
                if (pd != null) {
                    vo.setProcessName(pd.getName());
                    vo.setProcessKey(pd.getKey());
                }

                result.add(vo);
            }
        }

        PageResult<ProcessInstanceVO> pageResult = PageResult.of(result, total, pageNum, pageSize);
        return Result.success(pageResult);
    }

    // ==================== 7. 获取流程详情 ====================

    @GetMapping("/processes/get")
    @Operation(summary = "007_获取流程实例详情")
    
    public Result<ProcessDetailVO> getProcessDetail(
            @Parameter(description = "流程实例ID") @RequestParam String processInstanceId) {

        // 先从运行时查询
        ProcessInstance runtimeInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        ProcessDetailVO vo = new ProcessDetailVO();
        vo.setProcessInstanceId(processInstanceId);

        // 获取历史流程实例信息
        HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (historicInstance == null) {
            return Result.fail("流程实例不存在");
        }

        vo.setProcessDefinitionId(historicInstance.getProcessDefinitionId());
        vo.setInitiator(historicInstance.getStartUserId());
        vo.setStartTime(sdf.format(historicInstance.getStartTime()));

        // 获取流程定义信息
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicInstance.getProcessDefinitionId())
                .singleResult();
        if (pd != null) {
            vo.setProcessName(pd.getName());
            vo.setProcessKey(pd.getKey());
        }

        // 判断流程状态
        if (runtimeInstance != null) {
            vo.setStatus("running");

            // 获取当前任务
            Task currentTask = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (currentTask != null) {
                vo.setCurrentTaskName(currentTask.getName());
                vo.setCurrentAssignee(currentTask.getAssignee());
            }

            // 获取流程变量
            vo.setFormData(runtimeService.getVariables(processInstanceId));
        } else {
            vo.setStatus("completed");
            if (historicInstance.getEndTime() != null) {
                vo.setEndTime(sdf.format(historicInstance.getEndTime()));
            }
            if (historicInstance.getDurationInMillis() != null) {
                vo.setDuration(formatDuration(historicInstance.getDurationInMillis()));
            }

            // 获取历史变量
            List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            Map<String, Object> formData = new HashMap<>();
            for (HistoricVariableInstance var : vars) {
                formData.put(var.getName(), var.getValue());
            }
            vo.setFormData(formData);
        }

        // 获取审批历史节点
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        List<ProcessDetailVO.ApprovalNodeVO> historyNodes = activities.stream()
                .filter(act -> {
                    String type = act.getActivityType();
                    return type != null && (type.contains("Task") || type.contains("Gateway") || type.contains("Event"));
                })
                .map(act -> {
                    ProcessDetailVO.ApprovalNodeVO node = new ProcessDetailVO.ApprovalNodeVO();
                    node.setNodeId(act.getActivityId());
                    node.setNodeName(act.getActivityName());
                    node.setNodeType(act.getActivityType());
                    node.setAssignee(act.getAssignee());
                    node.setStartTime(sdf.format(act.getStartTime()));
                    if (act.getEndTime() != null) {
                        node.setEndTime(sdf.format(act.getEndTime()));
                        node.setDuration(formatDuration(act.getDurationInMillis()));
                    }
                    return node;
                }).collect(Collectors.toList());

        vo.setHistoryNodes(historyNodes);

        return Result.success(vo);
    }

    // ==================== 8. 启动流程 ====================

    @PostMapping("/processes/start")
    @Operation(summary = "008_启动流程实例")
    
    public Result<Map<String, String>> startProcess(@RequestBody StartProcessRequestDTO request) {

        Map<String, Object> variables = new HashMap<>();

        // 设置发起人
        if (request.getInitiator() != null) {
            variables.put("initiator", request.getInitiator());
        }

        // 设置标题
        if (request.getTitle() != null) {
            variables.put("title", request.getTitle());
        }

        // 合并其他变量
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }

        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                request.getProcessKey(),
                request.getBusinessKey(),
                variables
        );

        Map<String, String> result = new HashMap<>();
        result.put("processInstanceId", processInstance.getId());
        result.put("businessKey", processInstance.getBusinessKey());
        result.put("message", "流程启动成功");

        return Result.success(result);
    }

    // ==================== 9. 获取可启动的流程列表 ====================

    @GetMapping("/processes/definitions")
    @Operation(summary = "009_获取可启动的流程定义列表")
    
    public Result<List<Map<String, Object>>> getProcessDefinitions() {

        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .active()
                .latestVersion()
                .list();

        List<Map<String, Object>> result = definitions.stream().map(def -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", def.getId());
            map.put("key", def.getKey());
            map.put("name", def.getName());
            map.put("version", def.getVersion());
            map.put("description", def.getDescription());
            map.put("deploymentId", def.getDeploymentId());
            map.put("status", "active");
            map.put("createTime", "");
            map.put("updateTime", "");
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    // ==================== 10. 撤回/终止流程 ====================

    @DeleteMapping("/processes/{processInstanceId}")
    @Operation(summary = "010_终止/删除流程实例")
    
    public Result<String> deleteProcessInstance(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId,
            @Parameter(description = "删除原因") @RequestParam(required = false) String reason) {

        runtimeService.deleteProcessInstance(processInstanceId, reason != null ? reason : "用户主动删除");

        return Result.success("流程已终止");
    }

    // ==================== 工具方法 ====================

    private String formatDuration(long millis) {
        if (millis < 1000) {
            return millis + "毫秒";
        } else if (millis < 60000) {
            return (millis / 1000) + "秒";
        } else if (millis < 3600000) {
            return (millis / 60000) + "分钟";
        } else if (millis < 86400000) {
            return (millis / 3600000) + "小时";
        } else {
            return (millis / 86400000) + "天";
        }
    }
}
