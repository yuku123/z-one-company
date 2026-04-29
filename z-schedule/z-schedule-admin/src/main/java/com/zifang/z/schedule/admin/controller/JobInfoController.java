package com.zifang.z.schedule.admin.controller;

import com.zifang.z.schedule.admin.service.JobInfoService;
import com.zifang.z.schedule.core.model.JobInfo;
import com.zifang.z.schedule.core.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理Controller
 */
@RestController
@RequestMapping("/jobinfo")
public class JobInfoController {

    @Autowired
    private JobInfoService jobInfoService;

    /**
     * 获取任务列表
     */
    @GetMapping("/list")
    public ReturnT<List<JobInfo>> list(@RequestParam(required = false, defaultValue = "0") int jobGroup) {
        List<JobInfo> list;
        if (jobGroup > 0) {
            list = jobInfoService.getByJobGroup(jobGroup);
        } else {
            list = jobInfoService.getAll();
        }
        return ReturnT.success(list);
    }

    /**
     * 获取单个任务
     */
    @GetMapping("/{id}")
    public ReturnT<JobInfo> getById(@PathVariable int id) {
        JobInfo jobInfo = jobInfoService.getById(id);
        if (jobInfo == null) {
            return ReturnT.fail("任务不存在");
        }
        return ReturnT.success(jobInfo);
    }

    /**
     * 新增任务
     */
    @PostMapping("/add")
    public ReturnT<String> add(@RequestBody JobInfo jobInfo) {
        return jobInfoService.add(jobInfo);
    }

    /**
     * 更新任务
     */
    @PostMapping("/update")
    public ReturnT<String> update(@RequestBody JobInfo jobInfo) {
        return jobInfoService.update(jobInfo);
    }

    /**
     * 删除任务
     */
    @PostMapping("/remove/{id}")
    public ReturnT<String> remove(@PathVariable int id) {
        return jobInfoService.delete(id);
    }

    /**
     * 停止任务
     */
    @PostMapping("/stop/{id}")
    public ReturnT<String> stop(@PathVariable int id) {
        return jobInfoService.stop(id);
    }

    /**
     * 启动任务
     */
    @PostMapping("/start/{id}")
    public ReturnT<String> start(@PathVariable int id) {
        return jobInfoService.start(id);
    }

    /**
     * 触发任务执行
     */
    @PostMapping("/trigger/{id}")
    public ReturnT<String> trigger(@PathVariable int id) {
        return jobInfoService.trigger(id);
    }

    /**
     * 获取下次执行时间
     */
    @GetMapping("/nextTriggerTime")
    public ReturnT<List<String>> nextTriggerTime(@RequestParam String cron) {
        return jobInfoService.nextTriggerTime(cron);
    }
}
