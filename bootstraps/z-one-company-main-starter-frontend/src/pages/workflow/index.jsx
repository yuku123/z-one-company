import { Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from './Dashboard'

export default function WorkflowIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="dashboard" replace />} />
      <Route path="dashboard" element={<Dashboard />} />
    </Routes>
  )
}
