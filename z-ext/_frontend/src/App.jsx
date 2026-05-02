import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import ExtensionList from './pages/extension/ExtensionList'
import ImplementationList from './pages/implementation/ImplementationList'

// 简单的登录检查
const isAuthenticated = () => {
  return localStorage.getItem('zext_token') || sessionStorage.getItem('zext_token')
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
          <Route path="extension/list" element={<ExtensionList />} />
          <Route path="implementation/list" element={<ImplementationList />} />
          <Route path="route" element={<div>路由配置</div>} />
        </Route>
      </Routes>
    </div>
  )
}

export default App