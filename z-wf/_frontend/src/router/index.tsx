import { createBrowserRouter, Navigate } from 'react-router-dom';
import { Suspense, lazy } from 'react';
import { Spin } from 'antd';
import Layout from '@/components/Layout';

// 懒加载页面
const Dashboard = lazy(() => import('@/pages/Dashboard'));
const TodoList = lazy(() => import('@/pages/TodoList'));
const DoneList = lazy(() => import('@/pages/DoneList'));
const MyProcesses = lazy(() => import('@/pages/MyProcesses'));
const ProcessDetail = lazy(() => import('@/pages/ProcessDetail'));
const TaskDetail = lazy(() => import('@/pages/TaskDetail'));
const ProcessDesigner = lazy(() => import('@/pages/ProcessDesigner'));
const ProcessList = lazy(() => import('@/pages/ProcessList'));

// 加载中组件
const PageLoading = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
    <Spin size="large" tip="加载中..." />
  </div>
);

// 路由配置
export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        index: true,
        element: (
          <Suspense fallback={<PageLoading />}>
            <Dashboard />
          </Suspense>
        ),
      },
      {
        path: 'approval-center',
        children: [
          {
            path: 'todo',
            element: (
              <Suspense fallback={<PageLoading />}>
                <TodoList />
              </Suspense>
            ),
          },
          {
            path: 'done',
            element: (
              <Suspense fallback={<PageLoading />}>
                <DoneList />
              </Suspense>
            ),
          },
          {
            path: 'my-processes',
            element: (
              <Suspense fallback={<PageLoading />}>
                <MyProcesses />
              </Suspense>
            ),
          },
          {
            path: 'tasks/:taskId',
            element: (
              <Suspense fallback={<PageLoading />}>
                <TaskDetail />
              </Suspense>
            ),
          },
          {
            path: 'processes/:processInstanceId',
            element: (
              <Suspense fallback={<PageLoading />}>
                <ProcessDetail />
              </Suspense>
            ),
          },
        ],
      },
      {
        path: 'designer',
        children: [
          {
            path: '',
            element: (
              <Suspense fallback={<PageLoading />}>
                <ProcessList />
              </Suspense>
            ),
          },
          {
            path: ':processDefinitionId',
            element: (
              <Suspense fallback={<PageLoading />}>
                <ProcessDesigner />
              </Suspense>
            ),
          },
        ],
      },
      {
        path: '*',
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);
