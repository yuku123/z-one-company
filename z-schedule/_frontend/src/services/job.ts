import { http } from './api';
import {
  JobInfo,
  JobLog,
  JobGroup,
  PageData,
  DashboardStats,
  ScheduleRecord
} from '@/types';

// 任务管理API
export const jobApi = {
  // 获取任务列表
  getList: (params?: { jobGroup?: number; pageNum?: number; pageSize?: number }) =>
    http.get<PageData<JobInfo>>('/jobinfo/list', params),

  // 获取单个任务
  getById: (id: number) => http.get<JobInfo>(`/jobinfo/${id}`),

  // 新增任务
  add: (data: Partial<JobInfo>) => http.post<string>('/jobinfo/add', data),

  // 更新任务
  update: (data: Partial<JobInfo>) => http.post('/jobinfo/update', data),

  // 删除任务
  remove: (id: number) => http.post(`/jobinfo/remove/${id}`),

  // 停止任务
  stop: (id: number) => http.post(`/jobinfo/stop/${id}`),

  // 启动任务
  start: (id: number) => http.post(`/jobinfo/start/${id}`),

  // 手动触发
  trigger: (id: number) => http.post(`/jobinfo/trigger/${id}`),

  // 获取下次执行时间
  nextTriggerTime: (cron: string) =>
    http.get<string[]>('/jobinfo/nextTriggerTime', { cron }),
};

// 任务日志API
export const logApi = {
  // 获取日志列表
  getList: (params?: {
    jobId?: number;
    jobGroup?: number;
    status?: number;
    pageNum?: number;
    pageSize?: number;
    startTime?: string;
    endTime?: string;
  }) => http.get<PageData<JobLog>>('/joblog/list', params),

  // 获取日志详情
  getById: (id: number) => http.get<JobLog>(`/joblog/${id}`),

  // 获取执行日志
  getExecutionLog: (logId: number, fromLineNum: number = 0) =>
    http.get<{ content: string; fromLineNum: number; toLineNum: number; isEnd: boolean }>(
      '/joblog/executionLog',
      { logId, fromLineNum }
    ),

  // 清理日志
  clear: (jobId?: number, type?: number) =>
    http.post('/joblog/clear', { jobId, type }),
};

// 执行器管理API
export const groupApi = {
  // 获取执行器列表
  getList: () => http.get<JobGroup[]>('/jobgroup/list'),

  // 获取单个执行器
  getById: (id: number) => http.get<JobGroup>(`/jobgroup/${id}`),

  // 新增执行器
  add: (data: Partial<JobGroup>) => http.post('/jobgroup/add', data),

  // 更新执行器
  update: (data: Partial<JobGroup>) => http.post('/jobgroup/update', data),

  // 删除执行器
  remove: (id: number) => http.post(`/jobgroup/remove/${id}`),

  // 获取注册节点
  getRegistryNodes: (appName: string) =>
    http.get<string[]>(`/jobgroup/registryNodes`, { appName }),
};

// 仪表盘API
export const dashboardApi = {
  // 获取统计数据
  getStats: () => http.get<DashboardStats>('/dashboard/stats'),

  // 获取调度记录
  getScheduleRecords: (limit: number = 20) =>
    http.get<ScheduleRecord[]>('/dashboard/scheduleRecords', { limit }),

  // 获取任务成功率趋势
  getSuccessRateTrend: (days: number = 7) =>
    http.get<{ date: string; successRate: number; total: number }[]>(
      '/dashboard/successRateTrend',
      { days }
    ),

  // 获取执行器负载
  getExecutorLoad: () =>
    http.get<{ appName: string; instanceCount: number; runningTasks: number }[]>(
      '/dashboard/executorLoad'
    ),
};
