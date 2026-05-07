import { Routes, Route, Navigate } from 'react-router-dom'
import Overview from './overview'
import Dashboard from './dashboard'
import UserManagement from './user'
import RoleManagement from './role'
import PermissionManagement from './permission'
import TenantManagement from './tenant'
import DomainManagement from './domain'
import OrgManagement from './org'
import DeptManagement from './dept'
import GroupManagement from './group'
import DictManagement from './dict'
import AppManagement from './app'

export default function CtcIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="overview" replace />} />
      <Route path="overview" element={<Overview />} />
      <Route path="dashboard" element={<Dashboard />} />
      <Route path="user" element={<UserManagement />} />
      <Route path="role" element={<RoleManagement />} />
      <Route path="permission" element={<PermissionManagement />} />
      <Route path="audit" element={<div>审计日志</div>} />
      <Route path="tenant" element={<TenantManagement />} />
      <Route path="domain" element={<DomainManagement />} />
      <Route path="org" element={<OrgManagement />} />
      <Route path="dept" element={<DeptManagement />} />
      <Route path="group" element={<GroupManagement />} />
      <Route path="dict" element={<DictManagement />} />
      <Route path="app" element={<AppManagement />} />
    </Routes>
  )
}
