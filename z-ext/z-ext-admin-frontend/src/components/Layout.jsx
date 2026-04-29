import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout as AntLayout, Menu, Avatar, Dropdown, theme } from 'antd'
import {
  DashboardOutlined,
  ApiOutlined,
  ExperimentOutlined,
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

  const userInfo = JSON.parse(localStorage.getItem('zext_user') || '{}')

  const handleLogout = () => {
    localStorage.removeItem('zext_token')
    localStorage.removeItem('zext_user')
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
      key: 'extension',
      icon: <ApiOutlined />,
      label: '扩展点管理',
      children: [
        { key: '/extension/list', label: '扩展点列表' },
      ],
    },
    {
      key: 'implementation',
      icon: <ExperimentOutlined />,
      label: '实现管理',
      children: [
        { key: '/implementation/list', label: '实现列表' },
      ],
    },
    {
      key: '/route',
      icon: <SettingOutlined />,
      label: '路由配置',
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
            {collapsed ? 'Z' : 'Z-Ext'}
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
              Z-Ext 扩展平台
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