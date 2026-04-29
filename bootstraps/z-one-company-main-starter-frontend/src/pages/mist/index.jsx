import { Routes, Route, Navigate } from 'react-router-dom'
import SecretList from './secret/SecretList'

export default function MistIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="secret" replace />} />
      <Route path="secret" element={<SecretList />} />
    </Routes>
  )
}
