import request from './request'

const http: any = {
  get: (url: string, params?: any) => request.get(url, { params }),
  post: (url: string, data?: any) => request.post(url, data),
}

export interface JobInfo {
  id: number
  jobGroup: number
  jobDesc: string
  executorHandler: string
  executorParam: string
  executorBlockStrategy: string
  executorTimeout: number
  executorRetryCount: number
  glueType: string
  glueSource: string
  glueRemark: string
  childJobId: string
  triggerStatus: number
  triggerLastTime: number
  triggerNextTime: number
}

export interface JobLog {
  id: number
  jobGroup: number
  jobId: number
  executorAddress: string
  executorHandler: string
  executorParam: string
  executorShardingParam: string
  executorFailRetryCount: number
  triggerTime: string
  triggerStatus: number
  triggerMsg: string
  handleTime: string
  handleStatus: number
  handleMsg: string
  logContent: string
  exeSuccessCount: number
}

export interface JobGroup {
  id: number
  appName: string
  title: string
  order: number
}

export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface DashboardStats {
  jobCount: number
  triggerCount: number
  successRate: number
  executorCount: number
}

export interface ScheduleRecord {
  id: number
  jobId: number
  jobGroup: number
  executorAddress: string
  triggerTime: string
  triggerStatus: number
  triggerMsg: string
}

export const jobApi = {
  getList: (params?: any) => http.get<PageData<JobInfo>>('/jobinfo/list', params),
  getById: (id: number) => http.get<JobInfo>(`/jobinfo/${id}`),
  add: (data: Partial<JobInfo>) => http.post<string>('/jobinfo/add', data),
  update: (data: Partial<JobInfo>) => http.post('/jobinfo/update', data),
  remove: (id: number) => http.post(`/jobinfo/remove/${id}`),
  stop: (id: number) => http.post(`/jobinfo/stop/${id}`),
  start: (id: number) => http.post(`/jobinfo/start/${id}`),
  trigger: (id: number) => http.post(`/jobinfo/trigger/${id}`),
  nextTriggerTime: (cron: string) => http.get<string[]>('/jobinfo/nextTriggerTime', { cron }),
}

export const logApi = {
  getList: (params?: any) => http.get<PageData<JobLog>>('/joblog/list', params),
  getById: (id: number) => http.get<JobLog>(`/joblog/${id}`),
  clear: (jobId?: number, type?: number) => http.post('/joblog/clear', { jobId, type }),
}

export const groupApi = {
  getList: () => http.get<JobGroup[]>('/jobgroup/list'),
  getById: (id: number) => http.get<JobGroup>(`/jobgroup/${id}`),
  add: (data: Partial<JobGroup>) => http.post('/jobgroup/add', data),
  update: (data: Partial<JobGroup>) => http.post('/jobgroup/update', data),
  remove: (id: number) => http.post(`/jobgroup/remove/${id}`),
}

export const dashboardApi = {
  getStats: () => http.get<DashboardStats>('/dashboard/stats'),
  getScheduleRecords: (limit: number = 20) => http.get<ScheduleRecord[]>('/dashboard/scheduleRecords', { limit }),
  getSuccessRateTrend: (days: number = 7) => http.get<{ date: string; successRate: number; total: number }[]>(
    '/dashboard/successRateTrend', { days }
  ),
  getExecutorLoad: () => http.get<{ appName: string; instanceCount: number; runningTasks: number }[]>(
    '/dashboard/executorLoad'
  ),
}