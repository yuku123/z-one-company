package com.zifang.z.wf.starter.api;

import com.zifang.z.wf.core.service.LeaveProcessService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请假流程接口
 */
@RestController
@RequestMapping("/leave")
public class LeaveProcessController {

    @Resource
    private LeaveProcessService leaveProcessService;

    @Resource
    private TaskService taskService;

    /**
     * 启动请假流程
     * POST http://localhost:8080/leave/start
     * 请求体示例：
     * {
     *   "applicant": "zhangsan",
     *   "approver": "lisi",
     *   "days": 3,
     *   "reason": "生病请假"
     * }
     */
    @PostMapping("/start")
    public Map<String, String> startLeaveProcess(@RequestBody Map<String, Object> params) {
        String applicant = (String) params.get("applicant");
        String approver = (String) params.get("approver");

        // 封装流程变量（请假天数、原因）
        Map<String, Object> variables = new HashMap<>();
        variables.put("days", params.get("days"));
        variables.put("reason", params.get("reason"));

        // 启动流程
        String processInstanceId = leaveProcessService.startLeaveProcess(applicant, approver, variables);

        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("message", "流程启动成功");
        result.put("processInstanceId", processInstanceId);
        return result;
    }

    /**
     * 查询审批人待办任务
     * GET http://localhost:8080/leave/todo?approver=lisi
     */
    @GetMapping("/todo")
    public Map<String, Object> getTodoTasks(@RequestParam String approver) {
        List<Task> todoTasks = leaveProcessService.getTodoTasksByApprover(approver);

        Map<String, Object> result = new HashMap<>();
        result.put("code", "200");
        result.put("todoTasks", todoTasks);
        result.put("count", todoTasks.size());
        return result;
    }

    @GetMapping("/getApprovalTasks")
    public Map<String, Object> getApprovalTasks(@RequestParam String approver) {
        Map<String, Object> result = new HashMap<>();
        List<TodoTaskDTO> todoTaskDTOS = new ArrayList<>();

        // 1. 查询指定审批人的待办任务
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(approver)
                .processDefinitionKey("leaveProcess")
                .list();

        // 2. 将Camunda的Task实体转换为自定义DTO（仅保留业务字段）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Task task : tasks) {
            TodoTaskDTO dto = new TodoTaskDTO();
            dto.setTaskId(task.getId());
            dto.setTaskName(task.getName());
            dto.setAssignee(task.getAssignee());
            dto.setProcessInstanceId(task.getProcessInstanceId());
            dto.setProcessDefinitionId(task.getProcessDefinitionId());
//            dto.setProcessDefinitionName(task.getProcessDefinitionName());
            dto.setCreateTime(sdf.format(task.getCreateTime()));

            todoTaskDTOS.add(dto);
        }

        // 3. 返回封装结果（避免直接返回List，统一返回格式）
        result.put("code", 200);
        result.put("msg", "查询成功");
        result.put("todoTasks", todoTaskDTOS);
        return result;
    }


    /**
     * 完成审批任务
     * POST http://localhost:8080/leave/complete
     * 请求体示例：
     * {
     *   "taskId": "任务ID（从待办接口获取）",
     *   "approvalResult": "approved"  // approved/rejected
     * }
     */
    @PostMapping("/complete")
    public Map<String, String> completeApproval(@RequestBody Map<String, Object> params) {
        String taskId = (String) params.get("taskId");
        String approvalResult = (String) params.get("approvalResult");

        Map<String, Object> variables = new HashMap<>();
        variables.put("approvalResult", approvalResult);

        leaveProcessService.completeApprovalTask(taskId, variables);

        Map<String, String> result = new HashMap<>();
        result.put("code", "200");
        result.put("message", "审批完成");
        return result;
    }
}