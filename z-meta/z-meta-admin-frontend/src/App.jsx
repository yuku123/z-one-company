import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Layout from './components/Layout';
import Login from './pages/login';
import Dashboard from './pages/Dashboard';
import TenantList from './pages/tenant/TenantList';
import AppList from './pages/application/AppList';
import DictList from './pages/dict/DictList';
import ApiList from './pages/api/ApiList';
import './App.css';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Layout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="tenant" element={<TenantList />} />
            <Route path="app" element={<AppList />} />
            <Route path="dict" element={<DictList />} />
            <Route path="api" element={<ApiList />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;