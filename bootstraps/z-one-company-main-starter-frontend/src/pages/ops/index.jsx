import { Routes, Route, Navigate } from 'react-router-dom'
import Channel from './channel'
import Task from './task'
import Delivery from './delivery'
export default function OpsIndex() {
  return (<Routes><Route index element={<Navigate to="channel" replace />} />
    <Route path="channel" element={<Channel />} />
    <Route path="task" element={<Task />} />
    <Route path="delivery" element={<Delivery />} />
  </Routes>)
}
