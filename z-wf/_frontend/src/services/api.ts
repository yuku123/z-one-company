import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { message } from 'antd';
import {
  ProcessDefinition,
  ProcessInstance,
  Task,
  ApprovalRequest,
  DashboardStats,
  PageResult,
  FlowGraphData,
  ApprovalHistory,
} from '@/types/workflow';

// 创建 axios 实例
const api: AxiosInstance = axios.create({
  baseURL: '/',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response;
    if (data.code !== 200) {
      message.error(data.message || '请求失败');
      return Promise.reject(new Error(data.message));
    }
    return data.data;
  },
  (error) => {
    const { response } = error;
    if (response) {
      const { status, data } = response;
      switch (status) {
        case 401:
          message.error('未登录或登录已过期');
          // 可以在这里处理登出逻辑
          break;
        case 403:
          message.error('没有权限访问');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error('服务器内部错误');
          break;
        default:
          message.error(data?.message || '请求失败');
      }
    } else {
      message.error('网络错误，请检查网络连接');
    }
    return Promise.reject(error);
  }
);

// ==================== Approval Center API ====================

export const approvalApi = {
  // 001: 获取仪表盘统计
  getDashboardStats: (userId: string): Promise<DashboardStats> => {
    return api.get('/approval-center/dashboard', { params: { userId } });
  },

  // 002: 获取待办任务列表
  getTodoTasks: (userId: string, pageNum = 1, pageSize = 10): Promise<PageResult<Task>> => {
    return api.get('/approval-center/tasks/todo', {
      params: { userId, pageNum, pageSize },
    });
  },

  // 003: 获取已办任务列表
  getDoneTasks: (userId: string, pageNum = 1, pageSize = 10): Promise<PageResult<Task>> => {
    return api.get('/approval-center/tasks/done', {
      params: { userId, pageNum, pageSize },
    });
  },

  // 004: 获取任务详情
  getTaskDetail: (taskId: string): Promise<Task> => {
    return api.get(`/approval-center/tasks/${taskId}`);
  },

  // 005: 完成任务审批
  completeTask: (taskId: string, request: ApprovalRequest): Promise<void> => {
    return api.post(`/approval-center/tasks/${taskId}/complete`, request);
  },

  // 006: 获取我发起的流程列表
  getMyProcesses: (userId: string, pageNum = 1, pageSize = 10): Promise<PageResult<ProcessInstance>> => {
    return api.get('/approval-center/my-processes', {
      params: { userId, pageNum, pageSize },
    });
  },

  // 007: 获取流程实例详情
  getProcessDetail: (processInstanceId: string): Promise<ProcessInstance> => {
    return api.get(`/approval-center/processes/${processInstanceId}`);
  },

  // 008: 启动流程实例
  startProcess: (processDefinitionId: string, businessKey: string, variables?: Record<string, any>): Promise<ProcessInstance> => {
    return api.post('/approval-center/processes/start', {
      processDefinitionId,
      businessKey,
      variables,
    });
  },

  // 009: 获取流程定义列表
  getProcessDefinitions: (): Promise<ProcessDefinition[]> => {
    return api.get('/approval-center/processes/definitions');
  },

  // 010: 删除/终止流程实例
  deleteProcessInstance: (processInstanceId: string): Promise<void> => {
    return api.delete(`/approval-center/processes/${processInstanceId}`);
  },
};

// ==================== Designer API ====================

export const designerApi = {
  // 获取流程图数据
  getFlowGraph: (processDefinitionId: string): Promise<FlowGraphData> => {
    return api.get(`/designer/flow-graph/${processDefinitionId}`);
  },

  // 保存流程图
  saveFlowGraph: (processDefinitionId: string, data: FlowGraphData): Promise<void> => {
    return api.post(`/designer/flow-graph/${processDefinitionId}`, data);
  },

  // 部署流程定义
  deployProcess: (processDefinitionId: string): Promise<void> => {
    return api.post(`/designer/deploy/${processDefinitionId}`);
  },

  // 获取审批历史
  getApprovalHistory: (processInstanceId: string): Promise<ApprovalHistory[]> => {
    return api.get(`/designer/approval-history/${processInstanceId}`);
  },
};

export default api;
