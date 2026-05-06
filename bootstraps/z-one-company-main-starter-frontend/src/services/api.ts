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

// ========== 账号管理 ==========
export const getUserList = (params?: any) =>
  authRequest.post('/account/page', params || {})

export const getUserById = (id: string) =>
  authRequest.get('/account/get', { params: { id } })

export const createUser = (data: any) =>
  authRequest.post('/account', data)

export const updateUser = (id: string, data: any) =>
  authRequest.post(`/account/${id}/update`, data)

export const deleteUser = (id: string) =>
  authRequest.post(`/account/${id}/delete`, { id })

// ========== 角色管理 ==========
export const getRoleList = (params?: any) =>
  authRequest.post('/permission/role/page', params || {})

export const getRoleById = (id: string) =>
  authRequest.get('/permission/role/get', { params: { id } })

export const createRole = (data: any) =>
  authRequest.post('/permission/role', data)

export const updateRole = (id: string, data: any) =>
  authRequest.post(`/permission/role/${id}/update`, data)

export const deleteRole = (id: string) =>
  authRequest.post(`/permission/role/${id}/delete`, { id })

export const getRolePermissions = (roleId: string) =>
  authRequest.get('/permission/role/permissions', { params: { roleId } })

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
  authRequest.post(`/permission/${id}/update`, data)

export const deletePermission = (id: string) =>
  authRequest.post(`/permission/${id}/delete`, { id })

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
  authRequest.post(`/tenant/${tenantCode}/delete`)

// ========== 域管理 ==========
export const getDomainPage = (params?: any) =>
  authRequest.post('/domain/page', params)

export const getDomainListAll = () =>
  authRequest.get('/domain/list')

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
  authRequest.post(`/domain/${domainCode}/delete`)

// ========== 组织管理 ==========
export const getOrgPage = (params?: any) =>
  authRequest.post('/org/page', params)

export const getOrgListAll = () =>
  authRequest.get('/org/list')

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
  authRequest.post(`/org/${orgCode}/delete`)

// ========== 部门管理 ==========
export const getDeptPage = (params?: any) =>
  authRequest.post('/dept/page', params)

export const getDeptListAll = () =>
  authRequest.get('/dept/list')

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
  authRequest.post(`/dept/${deptCode}/delete`)

// ========== 组管理 ==========
export const getGroupPage = (params?: any) =>
  authRequest.post('/group/page', params)

export const getGroupListAll = () =>
  authRequest.get('/group/list')

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
  authRequest.post(`/group/${groupCode}/delete`)
