import { Routes, Route, Navigate } from 'react-router-dom'
import Domain from './domain'
import Ecs from './ecs'
import Rds from './rds'
import Oss from './oss'

export default function ResourceIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="domain" replace />} />
      <Route path="domain" element={<Domain />} />
      <Route path="ecs" element={<Ecs />} />
      <Route path="rds" element={<Rds />} />
      <Route path="oss" element={<Oss />} />
    </Routes>
  )
}
