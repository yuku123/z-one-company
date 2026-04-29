import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout as AntLayout, Menu, Avatar, Dropdown, theme } from 'antd'
import {
  DashboardOutlined,
  FileTextOutlined,
  CloudServerOutlined,
  ApartmentOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons'
import { Button } from 'antd'

const { Header, Sider, Content } = AntLayout

const Layout = () => {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken()

  const userInfo = JSON.parse(localStorage.getItem('zconfig_user') || '{}')

  const handleLogout = () => {
    localStorage.removeItem('zconfig_token')
    localStorage.removeItem('zconfig_user')
    navigate('/login')
  }

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ]

  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: '概览',
    },
    {
      key: 'config',
      icon: <FileTextOutlined />,
      label: '配置管理',
      children: [
        { key: '/config/list', label: '配置列表' },
        { key: '/config/history', label: '变更历史' },
      ],
    },
    {
      key: 'service',
      icon: <CloudServerOutlined />,
      label: '服务管理',
      children: [
        { key: '/service/list', label: '服务列表' },
      ],
    },
    {
      key: '/namespace',
      icon: <ApartmentOutlined />,
      label: '命名空间',
    },
    {
      key: '/system',
      icon: <SettingOutlined />,
      label: '系统设置',
    },
  ]

  const selectedKeys = [location.pathname]
  const pathParts = location.pathname.split('/').filter(Boolean)
  const openKeys = pathParts.length > 0 ? [pathParts[0]] : []

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        theme="light"
        style={{
          boxShadow: '2px 0 8px rgba(0,0,0,0.05)',
          zIndex: 10,
        }}
      >
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderBottom: '1px solid #f0f0f0',
          }}
        >
          <span
            style={{
              fontSize: collapsed ? 14 : 18,
              fontWeight: 'bold',
              color: '#1890ff',
            }}
          >
            {collapsed ? 'Z' : 'Z-Config'}
          </span>
        </div>
        <Menu
          mode="inline"
          selectedKeys={selectedKeys}
          defaultOpenKeys={openKeys}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
          style={{ borderRight: 0 }}
        />
      </Sider>
      <AntLayout>
        <Header
          style={{
            padding: '0 24px',
            background: colorBgContainer,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0,0,0,0.1)',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{ marginRight: 16 }}
            />
            <span style={{ fontSize: 16, fontWeight: 500 }}>
              Z-Config 配置中心
            </span>
          </div>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Avatar icon={<UserOutlined />} />
              <span>{userInfo.nickname || userInfo.username || '管理员'}</span>
              <DownOutlined style={{ fontSize: 12 }} />
            </div>
          </Dropdown>
        </Header>
        <Content
          style={{
            margin: 24,
            padding: 24,
            background: colorBgContainer,
            borderRadius: borderRadiusLG,
            minHeight: 280,
          }}
        >
          <Outlet />
        </Content>
      </AntLayout>
    </AntLayout>
  )
}

export default Layout
