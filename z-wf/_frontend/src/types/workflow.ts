// 工作流相关类型定义

// 流程定义
export interface ProcessDefinition {
  id: string;
  key: string;
  name: string;
  version: number;
  description?: string;
  createTime: string;
  updateTime: string;
  status: 'active' | 'suspended';
  category?: string;
}

// 流程实例
export interface ProcessInstance {
  id: string;
  processDefinitionId: string;
  processDefinitionName: string;
  startTime: string;
  endTime?: string;
  duration?: number;
  startUserId?: string;
  startUserName?: string;
  status: 'running' | 'completed' | 'suspended' | 'terminated';
  businessKey?: string;
}

// 任务
export interface Task {
  id: string;
  name: string;
  description?: string;
  processInstanceId: string;
  processDefinitionId: string;
  processDefinitionName: string;
  taskDefinitionKey: string;
  assignee?: string;
  assigneeName?: string;
  owner?: string;
  createTime: string;
  dueTime?: string;
  claimTime?: string;
  priority: number;
  status: 'pending' | 'claimed' | 'completed';
}

// 审批请求
export interface ApprovalRequest {
  action: 'approve' | 'reject' | 'transfer' | 'delegate';
  comment?: string;
  nextAssignee?: string;
  formData?: Record<string, any>;
}

// 仪表盘统计
export interface DashboardStats {
  todoCount: number;
  doneCount: number;
  initiatedCount: number;
  todoList: Task[];
}

// 分页结果
export interface PageResult<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

// 流程节点 (LogicFlow)
export interface FlowNode {
  id: string;
  type: string;
  x: number;
  y: number;
  text?: string;
  properties?: Record<string, any>;
}

// 流程边 (LogicFlow)
export interface FlowEdge {
  id: string;
  sourceNodeId: string;
  targetNodeId: string;
  text?: string;
  properties?: Record<string, any>;
}

// 流程图数据
export interface FlowGraphData {
  nodes: FlowNode[];
  edges: FlowEdge[];
}

// 审批历史记录
export interface ApprovalHistory {
  id: string;
  taskId: string;
  taskName: string;
  assignee: string;
  assigneeName: string;
  action: string;
  comment?: string;
  createTime: string;
  duration?: number;
}
