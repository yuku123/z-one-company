import { Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from './Dashboard'
import TaskList from './task/TaskList'
import TaskEdit from './task/TaskEdit'
import ProjectList from './project/ProjectList'
import UserList from './user/UserList'
import Login from './Login'

export default function TaskIndex() {
  return (
    <Routes>
      <Route path="login" element={<Login />} />
      <Route index element={<Navigate to="dashboard" replace />} />
      <Route path="dashboard" element={<Dashboard />} />
      <Route path="task" element={<TaskList />} />
      <Route path="task/edit/:id" element={<TaskEdit />} />
      <Route path="project" element={<ProjectList />} />
      <Route path="user" element={<UserList />} />
    </Routes>
  )
}