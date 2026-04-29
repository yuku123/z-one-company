// 任务状态
export enum JobStatus {
  STOPPED = 0,
  RUNNING = 1,
}

// 路由策略
export enum RouteStrategy {
  ROUND = 'ROUND',
  RANDOM = 'RANDOM',
  CONSISTENT_HASH = 'CONSISTENT_HASH',
  LRU = 'LRU',
  FAILOVER = 'FAILOVER',
  SHARDING_BROADCAST = 'SHARDING_BROADCAST',
}

// 阻塞策略
export enum BlockStrategy {
  SERIAL_EXECUTION = 'SERIAL_EXECUTION',
  DISCARD_LATER = 'DISCARD_LATER',
  COVER_EARLY = 'COVER_EARLY',
}

// 任务信息
export interface JobInfo {
  id: number;
  jobGroup: number;
  jobCron: string;
  jobDesc: string;
  addTime?: string;
  updateTime?: string;
  author?: string;
  alarmEmail?: string;
  executorRouteStrategy?: RouteStrategy;
  executorHandler?: string;
  executorParam?: string;
  executorBlockStrategy?: BlockStrategy;
  executorTimeout?: number;
  executorFailRetryCount?: number;
  logId?: number;
  triggerStatus: JobStatus;
  triggerLastTime?: number;
  triggerNextTime?: number;
}

// 任务日志
export interface JobLog {
  id: number;
  jobGroup: number;
  jobId: number;
  executorAddress?: string;
  executorHandler?: string;
  executorParam?: string;
  executorShardingParam?: string;
  executorFailRetryCount: number;
  triggerTime?: string;
  triggerCode: number;
  triggerMsg?: string;
  handleTime?: string;
  handleCode: number;
  handleMsg?: string;
  alarmStatus: number;
}

// 执行器组
export interface JobGroup {
  id: number;
  appName: string;
  title: string;
  order: number;
  addressType: number;
  addressList?: string;
  updateTime?: string;
  registryList?: string[];
}

// API响应
export interface ApiResponse<T> {
  code: number;
  msg: string;
  content?: T;
}

// 分页数据
export interface PageData<T> {
  total: number;
  list: T[];
  pageNum: number;
  pageSize: number;
  pages: number;
}

// 仪表盘统计数据
export interface DashboardStats {
  jobCount: number;
  runningJobCount: number;
  executorCount: number;
  todayTriggerCount: number;
  successRate: number;
  failRate: number;
}

// 调度记录
export interface ScheduleRecord {
  time: string;
  jobId: number;
  jobDesc: string;
  status: 'success' | 'fail' | 'running';
  duration?: number;
  message?: string;
}
