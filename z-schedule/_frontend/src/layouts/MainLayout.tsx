import { useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  Layout,
  Menu,
  Badge,
  Avatar,
  Dropdown,
  theme,
} from 'antd';
import {
  DashboardOutlined,
  ScheduleOutlined,
  FileTextOutlined,
  ClusterOutlined,
  SettingOutlined,
  BellOutlined,
  UserOutlined,
  DownOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import './MainLayout.css';

const { Header, Sider, Content } = Layout;

const menuItems = [
  {
    key: '/',
    icon: <DashboardOutlined />,
    label: '仪表盘',
  },
  {
    key: '/jobs',
    icon: <ScheduleOutlined />,
    label: '任务管理',
  },
  {
    key: '/logs',
    icon: <FileTextOutlined />,
    label: '调度日志',
  },
  {
    key: '/executors',
    icon: <ClusterOutlined />,
    label: '执行器管理',
  },
  {
    key: '/settings',
    icon: <SettingOutlined />,
    label: '系统设置',
  },
];

const userMenuItems = [
  {
    key: 'profile',
    icon: <UserOutlined />,
    label: '个人中心',
  },
  {
    key: 'settings',
    icon: <SettingOutlined />,
    label: '账号设置',
  },
  {
    type: 'divider',
  },
  {
    key: 'logout',
    icon: <LogoutOutlined />,
    label: '退出登录',
  },
];

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  const handleUserMenuClick = ({ key }: { key: string }) => {
    switch (key) {
      case 'profile':
        navigate('/profile');
        break;
      case 'settings':
        navigate('/settings');
        break;
      case 'logout':
        // 处理登出
        localStorage.removeItem('z-schedule-token');
        navigate('/login');
        break;
    }
  };

  return (
    <Layout className="main-layout">
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        theme="light"
        className="sidebar"
      >
        <div className="logo">
          <img src="/vite.svg" alt="Z-Schedule" />
          {!collapsed && <span className="title">Z-Schedule</span>}
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }} className="header">
          <div className="header-left">
            <span className="breadcrumb">
              {menuItems.find(item => item.key === location.pathname)?.label || 'Z-Schedule'}
            </span>
          </div>
          <div className="header-right">
            <Badge count={5} size="small">
              <BellOutlined className="icon" />
            </Badge>
            <Dropdown
              menu={{ items: userMenuItems, onClick: handleUserMenuClick }}
              placement="bottomRight"
            >
              <div className="user-info">
                <Avatar icon={<UserOutlined />} />
                <span className="username">管理员</span>
                <DownOutlined className="dropdown-icon" />
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content className="content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
