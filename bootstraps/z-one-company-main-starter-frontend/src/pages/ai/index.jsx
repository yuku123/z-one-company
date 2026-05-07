import { Routes, Route, Navigate } from 'react-router-dom'
import Mcp from './mcp'
import Skill from './skill'
import Agent from './agent'

export default function AiIndex() {
  return (
    <Routes>
      <Route index element={<Navigate to="mcp" replace />} />
      <Route path="mcp" element={<Mcp />} />
      <Route path="skill" element={<Skill />} />
      <Route path="agent" element={<Agent />} />
    </Routes>
  )
}
