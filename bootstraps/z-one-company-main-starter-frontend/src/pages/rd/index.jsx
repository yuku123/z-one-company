import { Routes, Route, Navigate } from 'react-router-dom'
import Repo from './repo'
import Sprint from './sprint'
import Ops from './ops'

export default function RdIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="repo" replace />} />
      <Route path="repo" element={<Repo />} />
      <Route path="sprint" element={<Sprint />} />
      <Route path="ops" element={<Ops />} />
    </Routes>
  )
}
