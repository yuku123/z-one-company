import { Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from './Dashboard'
import ConfigList from './ConfigList'
import ConfigEdit from './ConfigEdit'
import ConfigHistory from './ConfigHistory'

export default function ConfigIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="list" replace />} />
      <Route path="list" element={<ConfigList />} />
      <Route path="edit" element={<ConfigEdit />} />
      <Route path="history" element={<ConfigHistory />} />
      <Route path="dashboard" element={<Dashboard />} />
    </Routes>
  )
}