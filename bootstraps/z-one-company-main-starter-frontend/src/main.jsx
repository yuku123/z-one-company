import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import App from './App'
import Overview from './pages/Overview'
import ConfigIndex from './pages/config'
import TaskIndex from './pages/task'
import WorkflowIndex from './pages/workflow'
import CtcIndex from './pages/ctc'
import Login from './pages/ctc/login/AuthPage'
import ScheduleIndex from './pages/schedule'
import MistIndex from './pages/mist'
import MetaIndex from './pages/meta'
import AiIndex from './pages/ai'
import AgentAppList from './pages/ai/agent/app'
import AgentAppConfig from './pages/ai/agent/config'
import LlmProvider from './pages/ai/llm/provider'
import LlmModel from './pages/ai/llm/model'
import UsageDashboard from './pages/ai/usage'
import AgentSharePage from './pages/ai/agent/share'
import RdIndex from './pages/rd'
import ResourceIndex from './pages/resource'
import OpsIndex from './pages/ops'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<App />}>
            <Route index element={<Navigate to="/overview" replace />} />
            <Route path="overview" element={<Overview />} />
            <Route path="config/*" element={<ConfigIndex />} />
            <Route path="task/*" element={<TaskIndex />} />
            <Route path="workflow/*" element={<WorkflowIndex />} />
            <Route path="ctc/*" element={<CtcIndex />} />
            <Route path="schedule/*" element={<ScheduleIndex />} />
            <Route path="mist/*" element={<MistIndex />} />
            <Route path="meta/*" element={<MetaIndex />} />
            <Route path="ai" element={<AiIndex />}>
              <Route index element={<Navigate to="/ai/agent/app" replace />} />
              <Route path="agent/app" element={<AgentAppList />} />
              <Route path="agent/config" element={<AgentAppConfig />} />
              <Route path="llm/provider" element={<LlmProvider />} />
              <Route path="llm/model" element={<LlmModel />} />
              <Route path="usage" element={<UsageDashboard />} />
            </Route>
            <Route path="rd/*" element={<RdIndex />} />
            <Route path="resource/*" element={<ResourceIndex />} />
            <Route path="ops/*" element={<OpsIndex />} />
          </Route>
          <Route path="/share/:shareCode" element={<AgentSharePage />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  </React.StrictMode>,
)
