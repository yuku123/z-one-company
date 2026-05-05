import request from './request'
import { authRequest } from './request'

export const approvalApi = {
  getDashboardStats: (userId: string) => request.get('/approval-center/dashboard', { params: { userId } }),
  getTodoList: (userId: string, params?: any) => request.get('/approval-center/tasks/todo', { params: { userId, ...params } }),
  getDoneList: ( userId: string, params?: any) => request.get('/approval-center/tasks/done', { params: { userId, ...params } }),
  getTaskDetail: (taskId: string) => request.get(`/approval-center/tasks/${taskId}`),
  completeTask: (taskId: string, data: any) => request.post(`/approval-center/tasks/${taskId}/complete`, data),
  getMyProcesses: (userId: string, params?: any) => request.get('/approval-center/my-processes', { params: { userId, ...params } }),
  getProcessDetail: (processInstanceId: string) => request.get(`/approval-center/processes/${processInstanceId}`),
  startProcess: (data: any) => request.post('/approval-center/processes/start', data),
  getProcessDefinitions: () => request.get('/approval-center/processes/definitions'),
  deleteProcess: (processInstanceId: string) => request.delete(`/approval-center/processes/${processInstanceId}`),
}

export const designerApi = {
  getProcessList: () => request.get('/process/designer/list'),
  getProcessById: (id: string) => request.get(`/process/designer/${id}`),
  saveProcess: (data: any) => request.post('/process/designer/save', data),
  deployProcess: (id: string) => request.post(`/process/designer/${id}/deploy`),
  deleteProcess: (id: string) => request.delete(`/process/designer/${id}`),
}

export const login = (data: { username: string; password: string }) =>
  request.post('/auth/login', data)

export const getCurrentUser = () =>
  authRequest.get('/account/info')

export const getTenantList = () =>
  authRequest.get('/tenant/list')

export const getUserList = (params?: any) => {
  // IPage<User> 返回结构: { records, total, size, current }
  // ProTable 通过 responsePath: 'records' 读取
  return authRequest.post('/account/page', params || {})
}

export const getUserById = (id: string) =>
  authRequest.get(`/account/${id}`)

export const createUser = (data: any) =>
  authRequest.post('/account', data)

export const updateUser = (id: string, data: any) =>
  authRequest.put(`/account/${id}`, data)

export const deleteUser = (id: string) =>
  authRequest.delete(`/account/${id}`)

export const getRoleList = (params?: any) => {
  // IPage<Role> 返回结构: { records, total, size, current }
  return authRequest.post('/permission/role/page', params || {})
}

export const getRoleById = (id: string) =>
  authRequest.get(`/permission/role/${id}`)

export const createRole = (data: any) =>
  authRequest.post('/permission/role', data)

export const updateRole = (id: string, data: any) =>
  authRequest.put(`/permission/role/${id}`, data)

export const deleteRole = (id: string) =>
  authRequest.delete(`/permission/role/${id}`)

export const getPermissionList = async (params?: any) => {
  const res = await authRequest.get('/permission/list', { params })
  return { data: { records: res, total: res.length, current: 1 } }
}

export const createPermission = (data: any) =>
  authRequest.post('/permission', data)

export const updatePermission = (id: string, data: any) =>
  authRequest.put(`/permission/${id}`, data)

export const deletePermission = (id: string) =>
  authRequest.delete(`/permission/${id}`)

export const getAuditList = (params?: any) =>
  request.get('/audit/log', { params })

export const exportAudit = (params?: any) =>
  request.get('/audit/export', { params, responseType: 'blob' })

// 租户管理
export const getTenantPage = (params?: any) =>
  authRequest.get('/tenant/page', { params })

export const getTenantListAll = () =>
  authRequest.get('/tenant/list')

export const getTenantById = (id: string) =>
  authRequest.get(`/tenant/${id}`)

export const getTenantByCode = (tenantCode: string) =>
  authRequest.get(`/tenant/code/${tenantCode}`)

export const createTenant = (data: any) =>
  authRequest.post('/tenant', data)

export const updateTenant = (data: any) =>
  authRequest.put('/tenant', data)

export const deleteTenant = (id: string) =>
  authRequest.delete(`/tenant/${id}`)

// 域管理
export const getDomainPage = (params?: any) =>
  authRequest.get('/domain/page', { params })

export const getDomainListAll = () =>
  authRequest.get('/domain/list')

export const getDomainByTenantId = (tenantId: string) =>
  authRequest.get(`/domain/tenant/${tenantId}`)

export const getDomainById = (id: string) =>
  authRequest.get(`/domain/${id}`)

export const getDomainByCode = (domainCode: string) =>
  authRequest.get(`/domain/code/${domainCode}`)

export const createDomain = (data: any) =>
  authRequest.post('/domain', data)

export const updateDomain = (data: any) =>
  authRequest.put('/domain', data)

export const deleteDomain = (id: string) =>
  authRequest.delete(`/domain/${id}`)

// 组织管理
export const getOrgPage = (params?: any) =>
  authRequest.get('/org/page', { params })

export const getOrgListAll = () =>
  authRequest.get('/org/list')

export const getOrgByTenantId = (tenantId: string) =>
  authRequest.get(`/org/tenant/${tenantId}`)

export const getOrgByDomainId = (domainId: string) =>
  authRequest.get(`/org/domain/${domainId}`)

export const getOrgById = (id: string) =>
  authRequest.get(`/org/${id}`)

export const getOrgByCode = (orgId: string) =>
  authRequest.get(`/org/code/${orgId}`)

export const createOrg = (data: any) =>
  authRequest.post('/org', data)

export const updateOrg = (data: any) =>
  authRequest.put('/org', data)

export const deleteOrg = (id: string) =>
  authRequest.delete(`/org/${id}`)

// 部门管理
export const getDeptPage = (params?: any) =>
  authRequest.get('/dept/page', { params })

export const getDeptListAll = () =>
  authRequest.get('/dept/list')

export const getDeptByTenantId = (tenantId: string) =>
  authRequest.get(`/dept/tenant/${tenantId}`)

export const getDeptByDomainId = (domainId: string) =>
  authRequest.get(`/dept/domain/${domainId}`)

export const getDeptByOrgId = (orgId: string) =>
  authRequest.get(`/dept/org/${orgId}`)

export const getDeptById = (id: string) =>
  authRequest.get(`/dept/${id}`)

export const getDeptByCode = (deptId: string) =>
  authRequest.get(`/dept/code/${deptId}`)

export const createDept = (data: any) =>
  authRequest.post('/dept', data)

export const updateDept = (data: any) =>
  authRequest.put('/dept', data)

export const deleteDept = (id: string) =>
  authRequest.delete(`/dept/${id}`)

// 组管理
export const getGroupPage = (params?: any) =>
  authRequest.get('/group/page', { params })

export const getGroupListAll = () =>
  authRequest.get('/group/list')

export const getGroupByTenantId = (tenantId: string) =>
  authRequest.get(`/group/tenant/${tenantId}`)

export const getGroupByDomainId = (domainId: string) =>
  authRequest.get(`/group/domain/${domainId}`)

export const getGroupByOrgId = (orgId: string) =>
  authRequest.get(`/group/org/${orgId}`)

export const getGroupByDeptId = (deptId: string) =>
  authRequest.get(`/group/dept/${deptId}`)

export const getGroupById = (id: string) =>
  authRequest.get(`/group/${id}`)

export const getGroupByCode = (groupId: string) =>
  authRequest.get(`/group/code/${groupId}`)

export const createGroup = (data: any) =>
  authRequest.post('/group', data)

export const updateGroup = (data: any) =>
  authRequest.put('/group', data)

export const deleteGroup = (id: string) =>
  authRequest.delete(`/group/${id}`)