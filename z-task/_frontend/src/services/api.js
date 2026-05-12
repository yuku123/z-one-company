import request from '../utils/request'

// ========== 登录 ==========
export const login = (data) =>
  request.post('/user/login', data)

// ========== 任务管理 ==========
export const getTaskListByList = (listId) =>
  request.get('/api/task/list', { params: { listId } })

export const getTaskListByProject = (projectId) =>
  request.get(`/api/task/project/${projectId}`)

export const getTaskListByAssignee = (userId) =>
  request.get(`/api/task/assignee/${userId}`)

export const createTask = (data) =>
  request.post('/api/task', data)

export const moveTask = (taskId, targetListId, position) =>
  request.put('/api/task/move', null, { params: { taskId, targetListId, position } })

export const addTaskAssignee = (taskId, userId) =>
  request.post('/api/task/assignees', null, { params: { taskId, userId } })

export const removeTaskAssignee = (taskId, userId) =>
  request.delete(`/api/task/assignees/${taskId}/${userId}`)

export const completeTask = (taskId) =>
  request.post(`/api/task/${taskId}/complete`)

export const reopenTask = (taskId) =>
  request.post(`/api/task/${taskId}/reopen`)

// ========== 项目管理 ==========
export const getProjectListByUser = (userId) =>
  request.get(`/api/project/user/${userId}`)

export const createProject = (data) =>
  request.post('/api/project', data)

export const archiveProject = (projectId) =>
  request.put(`/api/project/${projectId}/archive`)
