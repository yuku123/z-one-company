import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConfigProvider, theme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';

import MainLayout from './layouts/MainLayout';
import Dashboard from './pages/Dashboard';
import JobList from './pages/JobList';

dayjs.locale('zh-cn');

function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        algorithm: theme.defaultAlgorithm,
        token: {
          colorPrimary: '#1677ff',
          borderRadius: 4,
        },
      }}
    >
      <Router>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<Dashboard />} />
            <Route path="jobs" element={<JobList />} />
          </Route>
        </Routes>
      </Router>
    </ConfigProvider>
  );
}

export default App;
