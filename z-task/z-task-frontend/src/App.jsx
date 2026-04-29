import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import TaskList from './pages/task/TaskList'
import TaskEdit from './pages/task/TaskEdit'
import ProjectList from './pages/project/ProjectList'
import UserList from './pages/user/UserList'
import Login from './pages/Login'
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Layout />}>
        <Route index element={<Dashboard />} />
        <Route path="task" element={<TaskList />} />
        <Route path="task/edit/:id" element={<TaskEdit />} />
        <Route path="project" element={<ProjectList />} />
        <Route path="user" element={<UserList />} />
      </Route>
    </Routes>
  )
}

export default App
