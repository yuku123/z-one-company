import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout as AntLayout, Menu, theme } from 'antd';
import {
  DashboardOutlined,
  AppstoreOutlined,
  UserOutlined,
  BookOutlined,
  ApiOutlined,
} from '@ant-design/icons';

const { Header, Content, Sider } = AntLayout;

const menuItems = [
  {
    key: '/dashboard',
    icon: <DashboardOutlined />,
    label: '首页',
  },
  {
    key: '/tenant',
    icon: <UserOutlined />,
    label: '租户管理',
  },
  {
    key: '/app',
    icon: <AppstoreOutlined />,
    label: '应用管理',
  },
  {
    key: '/dict',
    icon: <BookOutlined />,
    label: '字典管理',
  },
  {
    key: '/api',
    icon: <ApiOutlined />,
    label: '接口管理',
  },
];

function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const handleMenuClick = ({ key }) => {
    navigate(key);
  };

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Header
        style={{
          display: 'flex',
          alignItems: 'center',
          color: '#fff',
          fontSize: '18px',
          fontWeight: 'bold',
        }}
      >
        <div style={{ color: '#fff', marginRight: '50px' }}>z-meta 元数据平台</div>
      </Header>
      <AntLayout>
        <Sider width={200} style={{ background: colorBgContainer }}>
          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
            style={{ height: '100%', borderRight: 0 }}
            items={menuItems}
            onClick={handleMenuClick}
          />
        </Sider>
        <AntLayout style={{ padding: '16px' }}>
          <Content
            style={{
              padding: 24,
              margin: 0,
              minHeight: 280,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </Content>
        </AntLayout>
      </AntLayout>
    </AntLayout>
  );
}

export default Layout;