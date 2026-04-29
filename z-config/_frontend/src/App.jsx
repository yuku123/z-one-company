import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import ConfigList from './pages/config/ConfigList'
import ConfigEdit from './pages/config/ConfigEdit'
import ConfigHistory from './pages/config/ConfigHistory'
import ServiceList from './pages/service/ServiceList'
import './App.css'

// 简单的登录检查
const isAuthenticated = () => {
  return localStorage.getItem('zconfig_token') || sessionStorage.getItem('zconfig_token')
}

// 受保护的路由
const PrivateRoute = ({ children }) => {
  return isAuthenticated() ? children : <Navigate to="/login" replace />
}

function App() {
  return (
    <div className="app">
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={
          <PrivateRoute>
            <Layout />
          </PrivateRoute>
        }>
          <Route index element={<Dashboard />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="config/list" element={<ConfigList />} />
          <Route path="config/edit" element={<ConfigEdit />} />
          <Route path="config/history" element={<ConfigHistory />} />
          <Route path="service/list" element={<ServiceList />} />
        </Route>
      </Routes>
    </div>
  )
}

export default App
