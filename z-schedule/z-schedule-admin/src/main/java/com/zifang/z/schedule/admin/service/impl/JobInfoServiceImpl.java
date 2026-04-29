package com.zifang.z.schedule.admin.service.impl;

import com.zifang.z.schedule.admin.service.JobInfoService;
import com.zifang.z.schedule.core.model.JobInfo;
import com.zifang.z.schedule.core.model.ReturnT;
import com.zifang.z.schedule.core.util.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务信息服务实现类
 * 使用内存存储，实际生产环境应使用数据库存储
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {

    private static final Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);

    /**
     * 内存中的任务存储
     */
    private static final Map<Integer, JobInfo> jobInfoMap = new ConcurrentHashMap<>();

    /**
     * ID生成器
     */
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        logger.info("JobInfoService initialized");
    }

    @Override
    public JobInfo getById(int id) {
        return jobInfoMap.get(id);
    }

    @Override
    public List<JobInfo> getAll() {
        return new ArrayList<>(jobInfoMap.values());
    }

    @Override
    public List<JobInfo> getByJobGroup(int jobGroup) {
        List<JobInfo> result = new ArrayList<>();
        for (JobInfo jobInfo : jobInfoMap.values()) {
            if (jobInfo.getJobGroup() == jobGroup) {
                result.add(jobInfo);
            }
        }
        return result;
    }

    @Override
    public ReturnT<String> add(JobInfo jobInfo) {
        // 参数校验
        if (jobInfo.getJobDesc() == null || jobInfo.getJobDesc().trim().isEmpty()) {
            return ReturnT.fail("任务描述不能为空");
        }
        if (jobInfo.getJobCron() == null || jobInfo.getJobCron().trim().isEmpty()) {
            return ReturnT.fail("Cron表达式不能为空");
        }

        // 校验Cron表达式
        try {
            new CronExpression(jobInfo.getJobCron());
        } catch (ParseException e) {
            return ReturnT.fail("Cron表达式格式错误: " + e.getMessage());
        }

        // 生成ID
        int id = idGenerator.incrementAndGet();
        jobInfo.setId(id);

        // 设置默认值
        jobInfo.setTriggerStatus(0); // 默认停止状态
        jobInfo.setTriggerLastTime(0);
        jobInfo.setTriggerNextTime(0);
        jobInfo.setAddTime(new Date());
        jobInfo.setUpdateTime(new Date());

        // 保存到内存
        jobInfoMap.put(id, jobInfo);

        logger.info("Job added successfully, jobId: {}, jobDesc: {}", id, jobInfo.getJobDesc());
        return ReturnT.success(String.valueOf(id));
    }

    @Override
    public ReturnT<String> update(JobInfo jobInfo) {
        if (jobInfo.getId() <= 0) {
            return ReturnT.fail("任务ID不能为空");
        }

        JobInfo existJob = jobInfoMap.get(jobInfo.getId());
        if (existJob == null) {
            return ReturnT.fail("任务不存在");
        }

        // 如果任务正在运行，不允许修改Cron表达式
        if (existJob.getTriggerStatus() == 1) {
            if (jobInfo.getJobCron() != null && !jobInfo.getJobCron().equals(existJob.getJobCron())) {
                return ReturnT.fail("请先停止任务再修改Cron表达式");
            }
        }

        // 校验Cron表达式
        if (jobInfo.getJobCron() != null && !jobInfo.getJobCron().isEmpty()) {
            try {
                new CronExpression(jobInfo.getJobCron());
            } catch (ParseException e) {
                return ReturnT.fail("Cron表达式格式错误: " + e.getMessage());
            }
        }

        // 更新字段
        if (jobInfo.getJobGroup() > 0) {
            existJob.setJobGroup(jobInfo.getJobGroup());
        }
        if (jobInfo.getJobCron() != null) {
            existJob.setJobCron(jobInfo.getJobCron());
        }
        if (jobInfo.getJobDesc() != null) {
            existJob.setJobDesc(jobInfo.getJobDesc());
        }
        if (jobInfo.getAuthor() != null) {
            existJob.setAuthor(jobInfo.getAuthor());
        }
        if (jobInfo.getAlarmEmail() != null) {
            existJob.setAlarmEmail(jobInfo.getAlarmEmail());
        }
        if (jobInfo.getExecutorRouteStrategy() != null) {
            existJob.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        }
        if (jobInfo.getExecutorHandler() != null) {
            existJob.setExecutorHandler(jobInfo.getExecutorHandler());
        }
        if (jobInfo.getExecutorParam() != null) {
            existJob.setExecutorParam(jobInfo.getExecutorParam());
        }
        if (jobInfo.getExecutorBlockStrategy() != null) {
            existJob.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        }
        if (jobInfo.getExecutorTimeout() >= 0) {
            existJob.setExecutorTimeout(jobInfo.getExecutorTimeout());
        }
        if (jobInfo.getExecutorFailRetryCount() >= 0) {
            existJob.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
        }

        existJob.setUpdateTime(new Date());

        logger.info("Job updated successfully, jobId: {}", jobInfo.getId());
        return ReturnT.success();
    }

    @Override
    public ReturnT<String> delete(int id) {
        JobInfo jobInfo = jobInfoMap.get(id);
        if (jobInfo == null) {
            return ReturnT.fail("任务不存在");
        }

        // 如果任务正在运行，先停止
        if (jobInfo.getTriggerStatus() == 1) {
            return ReturnT.fail("请先停止任务再删除");
        }

        jobInfoMap.remove(id);
        logger.info("Job deleted successfully, jobId: {}", id);
        return ReturnT.success();
    }

    @Override
    public ReturnT<String> stop(int id) {
        JobInfo jobInfo = jobInfoMap.get(id);
        if (jobInfo == null) {
            return ReturnT.fail("任务不存在");
        }

        jobInfo.setTriggerStatus(0);
        jobInfo.setTriggerLastTime(0);
        jobInfo.setTriggerNextTime(0);
        jobInfo.setUpdateTime(new Date());

        logger.info("Job stopped successfully, jobId: {}", id);
        return ReturnT.success();
    }

    @Override
    public ReturnT<String> start(int id) {
        JobInfo jobInfo = jobInfoMap.get(id);
        if (jobInfo == null) {
            return ReturnT.fail("任务不存在");
        }

        // 校验Cron表达式
        try {
            new CronExpression(jobInfo.getJobCron());
        } catch (ParseException e) {
            return ReturnT.fail("Cron表达式格式错误: " + e.getMessage());
        }

        jobInfo.setTriggerStatus(1);
        jobInfo.setUpdateTime(new Date());

        logger.info("Job started successfully, jobId: {}", id);
        return ReturnT.success();
    }

    @Override
    public ReturnT<String> trigger(int id) {
        JobInfo jobInfo = jobInfoMap.get(id);
        if (jobInfo == null) {
            return ReturnT.fail("任务不存在");
        }

        // TODO: 实现任务触发逻辑
        logger.info("Job triggered manually, jobId: {}", id);
        return ReturnT.success("任务触发成功");
    }

    @Override
    public ReturnT<List<String>> nextTriggerTime(String cron) {
        List<String> result = new ArrayList<>();
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    result.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTime));
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            return ReturnT.fail("Cron表达式格式错误: " + e.getMessage());
        }
        return ReturnT.success(result);
    }
}
