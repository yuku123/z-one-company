import request from './request'
import { authRequest } from './request'

export const approvalApi = {
  getDashboardStats: (userId: string) => request.get('/approval-center/dashboard', { params: { userId } }),
  getTodoList: (userId: string, params?: any) => request.get('/approval-center/tasks/todo', { params: { userId, ...params } }),
  getDoneList: ( userId: string, params?: any) => request.get('/approval-center/tasks/done', { params: { userId, ...params } }),
  getTaskDetail: (taskId: string) => request.get('/approval-center/tasks/get', { params: { taskId } }),
  completeTask: (taskId: string, data: any) => request.post('/approval-center/tasks/complete', data, { params: { taskId } }),
  getMyProcesses: (userId: string, params?: any) => request.get('/approval-center/my-processes', { params: { userId, ...params } }),
  getProcessDetail: (processInstanceId: string) => request.get('/approval-center/processes/get', { params: { processInstanceId } }),
  startProcess: (data: any) => request.post('/approval-center/processes/start', data),
  getProcessDefinitions: () => request.get('/approval-center/processes/definitions'),
  deleteProcess: (processInstanceId: string) => request.post('/approval-center/processes/remove', null, { params: { processInstanceId } }),
}

export const designerApi = {
  getProcessList: () => request.get('/process/designer/list'),
  getProcessById: (id: string) => request.get('/process/designer/get', { params: { id } }),
  saveProcess: (data: any) => request.post('/process/designer/save', data),
  deployProcess: (id: string) => request.post('/process/designer/deploy', null, { params: { id } }),
  deleteProcess: (id: string) => request.post('/process/designer/remove', null, { params: { id } }),
}

export const login = (data: { username: string; password: string }) =>
  request.post('/auth/login', data)

export const getCurrentUser = () =>
  authRequest.get('/account/info')

export const getTenantList = () =>
  authRequest.get('/tenant/list')

// ========== 账号管理 ==========
export const getUserList = (params?: any) =>
  authRequest.post('/account/page', params || {})

export const getUserById = (id: string) =>
  authRequest.get('/account/get', { params: { id } })

export const createUser = (data: any) =>
  authRequest.post('/account', data)

export const updateUser = (id: string, data: any) =>
  authRequest.post('/account/update', data, { params: { id } })

export const deleteUser = (id: string) =>
  authRequest.post('/account/delete', null, { params: { id } })

// ========== 角色管理 ==========
export const getRoleList = (params?: any) =>
  authRequest.post('/permission/role/page', params || {})

export const getRoleById = (id: string) =>
  authRequest.get('/permission/role/get', { params: { id } })

export const createRole = (data: any) =>
  authRequest.post('/permission/role', data)

export const updateRole = (id: string, data: any) =>
  authRequest.post('/permission/role/update', data, { params: { id } })

export const deleteRole = (id: string) =>
  authRequest.post('/permission/role/delete', null, { params: { id } })

export const getRolePermissions = (roleId: string) =>
  authRequest.get('/permission/role/permissions', { params: { roleId } })

export const assignRolePermissions = (roleId: string | number, permissionIds: (string | number)[]) =>
  authRequest.post(`/permission/role/${roleId}/permissions`, permissionIds)

// ========== 权限管理 ==========
export const getPermissionList = async (params?: any) => {
  const res = await authRequest.get('/permission/list', { params })
  return { data: { records: res, total: res.length, current: 1 } }
}

export const createPermission = (data: any) =>
  authRequest.post('/permission', data)

export const getPermissionById = (id: string) =>
  authRequest.get('/permission/get', { params: { id } })

export const updatePermission = (id: string, data: any) =>
  authRequest.post('/permission/update', data, { params: { id } })

export const deletePermission = (id: string) =>
  authRequest.post('/permission/delete', null, { params: { id } })

// ========== 审计日志 ==========
export const getAuditList = (params?: any) =>
  request.get('/audit/log', { params })

export const exportAudit = (params?: any) =>
  request.get('/audit/export', { params, responseType: 'blob' })

// ========== 租户管理 ==========
export const getTenantPage = (params?: any) =>
  authRequest.post('/tenant/page', params)

export const getTenantListAll = () =>
  authRequest.get('/tenant/list')

export const getTenantByCode = (tenantCode: string) =>
  authRequest.get(`/tenant/code/${tenantCode}`)

export const createTenant = (data: any) =>
  authRequest.post('/tenant', data)

export const updateTenant = (data: any) =>
  authRequest.post('/tenant/update', data)

export const deleteTenant = (tenantCode: string) =>
  authRequest.post('/tenant/delete', null, { params: { tenantCode } })

// ========== 域管理 ==========
export const getDomainPage = (params?: any) =>
  authRequest.post('/domain/page', params)

export const getDomainListAll = () =>
  authRequest.get('/domain/list')
export const getDomainList = getDomainListAll

// 根据租户编码查域列表（App.jsx 租户切换用）
export const getDomainByTenantCode = (tenantCode: string) =>
  authRequest.get(`/domain/tenant/${tenantCode}`)

export const getDomainByCode = (domainCode: string) =>
  authRequest.get(`/domain/code/${domainCode}`)

export const createDomain = (data: any) =>
  authRequest.post('/domain', data)

export const updateDomain = (data: any) =>
  authRequest.post('/domain/update', data)

export const deleteDomain = (domainCode: string) =>
  authRequest.post('/domain/delete', null, { params: { domainCode } })

// ========== 组织管理 ==========
export const getOrgPage = (params?: any) =>
  authRequest.post('/org/page', params)

export const getOrgListAll = () =>
  authRequest.get('/org/list')
export const getOrgList = getOrgListAll

// 根据租户编码查组织
export const getOrgByTenantCode = (tenantCode: string) =>
  authRequest.get(`/org/tenant/${tenantCode}`)

// 根据域编码查组织
export const getOrgByDomainCode = (domainCode: string) =>
  authRequest.get(`/org/domain/${domainCode}`)

export const getOrgByCode = (orgCode: string) =>
  authRequest.get(`/org/code/${orgCode}`)

export const createOrg = (data: any) =>
  authRequest.post('/org', data)

export const updateOrg = (data: any) =>
  authRequest.post('/org/update', data)

export const deleteOrg = (orgCode: string) =>
  authRequest.post('/org/delete', null, { params: { orgCode } })

// ========== 部门管理 ==========
export const getDeptPage = (params?: any) =>
  authRequest.post('/dept/page', params)

export const getDeptListAll = () =>
  authRequest.get('/dept/list')
export const getDeptList = getDeptListAll

export const getDeptByTenantCode = (tenantCode: string) =>
  authRequest.get(`/dept/tenant/${tenantCode}`)

export const getDeptByDomainCode = (domainCode: string) =>
  authRequest.get(`/dept/domain/${domainCode}`)

export const getDeptByOrgCode = (orgCode: string) =>
  authRequest.get(`/dept/org/${orgCode}`)

export const getDeptByCode = (deptCode: string) =>
  authRequest.get(`/dept/code/${deptCode}`)

export const createDept = (data: any) =>
  authRequest.post('/dept', data)

export const updateDept = (data: any) =>
  authRequest.post('/dept/update', data)

export const deleteDept = (deptCode: string) =>
  authRequest.post('/dept/delete', null, { params: { deptCode } })

// ========== 组管理 ==========
export const getGroupPage = (params?: any) =>
  authRequest.post('/group/page', params)

export const getGroupListAll = () =>
  authRequest.get('/group/list')
export const getGroupList = getGroupListAll

export const getGroupByTenantCode = (tenantCode: string) =>
  authRequest.get(`/group/tenant/${tenantCode}`)

export const getGroupByDomainCode = (domainCode: string) =>
  authRequest.get(`/group/domain/${domainCode}`)

export const getGroupByOrgCode = (orgCode: string) =>
  authRequest.get(`/group/org/${orgCode}`)

export const getGroupByDeptCode = (deptCode: string) =>
  authRequest.get(`/group/dept/${deptCode}`)

export const getGroupByCode = (groupCode: string) =>
  authRequest.get(`/group/code/${groupCode}`)

export const createGroup = (data: any) =>
  authRequest.post('/group', data)

export const updateGroup = (data: any) =>
  authRequest.post('/group/update', data)

export const deleteGroup = (groupCode: string) =>
  authRequest.post('/group/delete', null, { params: { groupCode } })

// ==================== 用户-组织关联 ====================
export const userOrgRelApi = {
  usersByGroup: (groupCode: string) => request.get('/user-org/users-by-group', { params: { groupCode } }),
  usersByDept: (deptCode: string) => request.get('/user-org/users-by-dept', { params: { deptCode } }),
  bind: (data: any) => request.post('/user-org/bind', data),
  clearUser: (userId: number) => request.post('/user-org/user/remove', null, { params: { userId } }),
}

// ==================== 元典字典 ====================
export const dictApi = {
  list: (tenantCode: string) => request.get('/dict/list', { params: { tenantCode } }),
  categories: (tenantCode: string) => request.get('/dict/categories', { params: { tenantCode } }),
  hasInit: (tenantCode: string) => request.get('/dict/has-init', { params: { tenantCode } }),
  init: (tenantCode: string, domainCode: string) => request.post('/dict/init', { tenantCode, domainCode }),
  add: (data: any) => request.post('/dict', data),
  update: (data: any) => request.post('/dict/update', data),
  delete: (id: number) => request.post('/dict/delete', null, { params: { id } }),
  reorder: (data: any[]) => request.post('/dict/reorder', data),
}

// ==================== 应用管理 ====================
export const appApi = {
  // App CRUD
  list: (params?: any) => request.get('/app/list', { params }),
  create: (data: any) => request.post('/app', data),
  update: (data: any) => request.post('/app/update', data),
  delete: (id: number) => request.post('/app/delete', null, { params: { id } }),
  // Menu CRUD
  menuList: (appCode: string) => request.get('/app/menu/list', { params: { appCode } }),
  createMenu: (data: any) => request.post('/app/menu', data),
  updateMenu: (data: any) => request.post('/app/menu/update', data),
  deleteMenu: (id: number) => request.post('/app/menu/delete', null, { params: { id } }),
}

// ==================== 字典分类 ====================
export const dictCatApi = {
  list: (tenantCode: string) => request.get('/dict-category/list', { params: { tenantCode } }),
  create: (data: any) => request.post('/dict-category', data),
  update: (data: any) => request.post('/dict-category/update', data),
  delete: (id: number) => request.post('/dict-category/delete', null, { params: { id } }),
}

// ==================== Config 配置中心 ====================
export const configApi = {
  // 配置列表分页
  pageConfig: (params: any) => request.post('/config/pageConfig', params),
  // 保存配置
  saveConfig: (data: any) => request.post('/config/saveConfig', data),
  // 删除配置
  deleteConfig: (data: any) => request.post('/config/delete', data),
  // 获取 Group 列表
  groupList: () => request.get('/config/groupList'),
  // 获取所有命名空间
  namespaceList: () => request.get('/config/namespaceList'),
  // 配置变更历史
  historyPage: (params: any) => request.post('/config/history/page', params),
  // Dashboard 统计
  getStats: () => request.get('/dashboard/stats'),
  // 集群列表
  clusterList: () => request.get('/cluster/list'),
  // 集群新增/更新
  clusterSave: (data: any) => request.post('/cluster/save', data),
  // 集群删除
  clusterDelete: (id: number) => request.post('/cluster/delete', null, { params: { id } }),
}

// ==================== Naming 服务注册发现 ====================
export const namingApi = {
  // 服务列表
  listServices: () => request.get('/naming/listServices'),
  // 查询实例（按服务名）
  getInstances: (serviceName: string, group?: string) => request.get('/naming/getAllInstances', { params: { serviceName, group } }),
  // 查询健康实例
  getHealthyInstances: (serviceName: string) =>
    request.get('/naming/selectInstances/healthy', { params: { serviceName } }),
  // 服务详情
  serviceDetail: (serviceName: string) => request.get(`/naming/service/${serviceName}`),
  // 注册实例
  registerInstance: (data: any) => request.post('/naming/registerInstance', data),
  // 注销实例
  deregisterInstance: (data: any) => request.post('/naming/deregisterInstance', data),
}

// ==================== 任务中心 ====================
export const taskApi = {
  // 任务列表（按列表）
  getTaskListByList: (listId: number) =>
    request.get('/task/list', { params: { listId } }),
  // 任务列表（按项目）
  getTaskListByProject: (projectId: number) =>
    request.get('/task/project/list', { params: { projectId } }),
  // 任务列表（按负责人）
  getTaskListByAssignee: (userId: string) =>
    request.get('/task/assignee/list', { params: { userId } }),
  // 创建任务
  createTask: (data: any) =>
    request.post('/task', data),
  // 移动任务
  moveTask: (taskId: number, targetListId: number, position: string) =>
    request.put('/task/move', null, { params: { taskId, targetListId, position } }),
  // 添加执行者
  addTaskAssignee: (taskId: number, userId: string) =>
    request.post('/task/assignees', null, { params: { taskId, userId } }),
  // 移除执行者
  removeTaskAssignee: (taskId: number, userId: string) =>
    request.post('/task/assignees/remove', null, { params: { taskId, userId } }),
  // 完成任务
  completeTask: (taskId: number) =>
    request.post('/task/complete', null, { params: { taskId } }),
  // 重新打开任务
  reopenTask: (taskId: number) =>
    request.post('/task/reopen', null, { params: { taskId } }),
}

// ==================== 项目管理 ====================
export const projectApi = {
  // 获取用户项目列表
  getProjectListByUser: (userId: string) =>
    request.get('/project/user/list', { params: { userId } }),
  // 创建项目
  createProject: (data: any) =>
    request.post('/project', data),
  // 归档项目
  archiveProject: (projectId: number) =>
    request.put('/project/archive', null, { params: { projectId } }),
}

// ==================== 镜像仓库中心 ====================
export const opsApi = {
  // 镜像仓库 CRUD
  listImage: (params) => request.get('/api/image/list', { params }),
  pageImage: (data) => request.post('/api/image/page', data),
  getImage: (params) => request.get('/api/image/get', { params }),
  addImage: (data) => request.post('/api/image', data),
  updateImage: (data) => request.post('/api/image/update', data),
  deleteImage: (params) => request.post('/api/image/delete', params),
  // 镜像版本
  listImageTags: (params) => request.get('/api/image/tags', { params }),
  addImageTag: (data) => request.post('/api/image/tag', data),
  deleteImageTag: (params) => request.post('/api/image/tag/delete', null, { params }),
  // 构建记录
  listImageBuild: (params) => request.get('/api/image-build/list', { params }),
  pageImageBuild: (data) => request.post('/api/image-build/page', data),
  getImageBuild: (params) => request.get('/api/image-build/get', { params }),
  addImageBuild: (data) => request.post('/api/image-build', data),
  getBuildLogs: (params) => request.get('/api/image-build/logs', { params }),
}

// ========== 技能市场 ==========
export const skillApi = {
  page: (data: any) => request.post('/api/skill/page', data),
  getBySkillCode: (skillCode: string) => request.get('/api/skill/get', { params: { skillCode } }),
  getContent: (skillCode: string) => request.get('/api/skill/content', { params: { skillCode } }),
  create: (data: any) => request.post('/api/skill', data),
  update: (data: any) => request.post('/api/skill/update', data),
  delete: (id: number) => request.post('/api/skill/delete', null, { params: { id } }),
  publish: (skillCode: string) => request.post('/api/skill/publish', { skillCode }),
  install: (data: any) => request.post('/api/skill/install', data),
  versions: (skillCode: string) => request.get('/api/skill/versions', { params: { skillCode } }),
  addVersion: (data: any) => request.post('/api/skill/version', data),
  categoryTree: () => request.get('/api/skill/category/tree'),
  createCategory: (data: any) => request.post('/api/skill/category', data),
  deleteCategory: (id: number) => request.post('/api/skill/category/delete', null, { params: { id } }),
  hot: (limit: number = 10) => request.get('/api/skill/hot', { params: { limit } }),
  stats: () => request.get('/api/skill/stats'),
  // Package support
  uploadPackage: (skillCode: string, version: string, file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('version', version)
    return request.post('/api/skill/package/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      params: { skillCode },
    })
  },
  downloadPackageUrl: (skillCode: string) => `/api/skill/package/download?skillCode=${skillCode}`,
}

// ==================== MCP 管理 ====================
export const mcpApi = {
  // Server CRUD
  list: (tenantCode?: string) => request.get('/api/mcp/server/list', { params: { tenantCode } }),
  get: (id: number) => request.get('/api/mcp/server/get', { params: { id } }),
  create: (data: any) => request.post('/api/mcp/server', data),
  update: (data: any) => request.post('/api/mcp/server/update', data),
  delete: (id: number) => request.post('/api/mcp/server/delete', null, { params: { id } }),
  // Tool proxy
  listTools: (id: number) => request.post('/api/mcp/server/tools/list', null, { params: { id } }),
  callTool: (id: number, name: string, args?: any) =>
    request.post('/api/mcp/server/tools/call', { name, arguments: args || {} }, { params: { id } }),
  // Test connection
  test: (id: number) => request.post('/api/mcp/server/test', null, { params: { id } }),
}

// ==================== Agent 应用中心 ====================
export const agentApi = {
  // App CRUD
  appPage: (data: any) => request.post('/api/agent/app/page', data),
  appGet: (appCode: string) => request.get('/api/agent/app/get', { params: { appCode } }),
  appCreate: (data: any) => request.post('/api/agent/app', data),
  appUpdate: (data: any) => request.post('/api/agent/app/update', data),
  appDelete: (id: number) => request.post('/api/agent/app/delete', null, { params: { id } }),
  appPublish: (appCode: string) => request.post('/api/agent/app/publish', { appCode }),
  appVersions: (appCode: string) => request.get('/api/agent/app/versions', { params: { appCode } }),
  appDraftGet: (appCode: string) => request.get('/api/agent/app/draft', { params: { appCode } }),
  appDraftSave: (data: any) => request.post('/api/agent/app/draft', data),

  // Instance
  instanceCreate: (data: any) => request.post('/api/agent/instance/create', data),
  instanceGet: (instanceCode: string) => request.get('/api/agent/instance/get', { params: { instanceCode } }),
  instanceList: (ownerId: string) => request.get('/api/agent/instance/list', { params: { ownerId } }),
  instanceStatus: (instanceCode: string, status: string) =>
    request.post('/api/agent/instance/status', null, { params: { instanceCode, status } }),

  // Share
  shareCreate: (data: any) => request.post('/api/agent/share/create', data),
  shareVerify: (shareCode: string) => request.get('/api/agent/share/verify', { params: { shareCode } }),
  shareList: (instanceCode: string) => request.get('/api/agent/share/list', { params: { instanceCode } }),
  shareDisable: (shareCode: string) => request.post('/api/agent/share/disable', null, { params: { shareCode } }),

  // Chat
  chatSend: (data: any) => request.post('/api/agent/chat/send', data),
  chatHistory: (instanceCode: string, limit?: number) =>
    request.get('/api/agent/chat/history', { params: { instanceCode, limit: limit || 50 } }),
  chatClear: (instanceCode: string) => request.post('/api/agent/chat/clear', null, { params: { instanceCode } }),
}
