import React, { useRef, useState, useEffect } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  Layout as AntLayout,
  Menu,
  Avatar,
  Dropdown,
  Space,
  Typography,
  Cascader,
  Card,
  Divider,
} from 'antd'
import {
  DashboardOutlined,
  TeamOutlined,
  SafetyOutlined,
  KeyOutlined,
  AuditOutlined,
  HomeOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons'
import styles from './Layout.module.css'
import { getCurrentUser } from '../../services/api'

const { Header, Sider, Content } = AntLayout
const { Text } = Typography

const Layout = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const [collapsed, setCollapsed] = useState(false)
  const [userName, setUserName] = useState('管理员')
  const [userInfo, setUserInfo] = useState(null)
  const [tenantOptions, setTenantOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState([])

  const navigateRef = useRef(navigate)
  navigateRef.current = navigate

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigateRef.current('/login')
      return
    }
    
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      try {
        const user = JSON.parse(userInfoStr)
        setUserName(user.userName || '管理员')
        setUserInfo(user)
        if (user.tenantCode) {
          setSelectedTenant([user.tenantCode])
        }
      } catch (e) {}
    }
    getCurrentUser().then(res => {
      if (res) {
        setUserInfo(res)
        setUserName(res.userName || '管理员')
        localStorage.setItem('userInfo', JSON.stringify(res))
      }
    }).catch(() => {})
    setTenantOptions([
      {
        value: 'tenant1',
        label: '租户1',
        children: [
          { value: 'domain1-1', label: '域1-1' },
          { value: 'domain1-2', label: '域1-2' },
        ],
      },
      {
        value: 'tenant2',
        label: '租户2',
        children: [
          { value: 'domain2-1', label: '域2-1' },
          { value: 'domain2-2', label: '域2-2' },
        ],
      },
    ])
  }, [])

  // 获取当前选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname
    if (path.includes('/overview')) return ['overview']
    if (path.includes('/dashboard')) return ['dashboard']
    if (path.includes('/user')) return ['user']
    if (path.includes('/role')) return ['role']
    if (path.includes('/permission')) return ['permission']
    if (path.includes('/audit')) return ['audit']
    if (path.includes('/tenant')) return ['tenant']
    if (path.includes('/domain')) return ['domain']
    if (path.includes('/org')) return ['org']
    if (path.includes('/dept')) return ['dept']
    if (path.includes('/group')) return ['group']
    return ['overview']
  }

  // 菜单项
  const menuItems = [
    {
      key: 'overview',
      icon: <HomeOutlined />,
      label: '概览',
    },
    {
      key: 'dashboard',
      icon: <DashboardOutlined />,
      label: '工作台',
    },
    {
      type: 'divider',
    },
    {
      key: 'user',
      icon: <TeamOutlined />,
      label: '用户管理',
    },
    {
      key: 'role',
      icon: <SafetyOutlined />,
      label: '角色管理',
    },
    {
      key: 'permission',
      icon: <KeyOutlined />,
      label: '权限管理',
    },
    {
      key: 'audit',
      icon: <AuditOutlined />,
      label: '审计日志',
    },
    {
      type: 'divider',
    },
    {
      key: 'tenant',
      icon: <TeamOutlined />,
      label: '租户管理',
    },
    {
      key: 'domain',
      icon: <TeamOutlined />,
      label: '域管理',
    },
    {
      key: 'org',
      icon: <TeamOutlined />,
      label: '组织管理',
    },
    {
      key: 'dept',
      icon: <TeamOutlined />,
      label: '部门管理',
    },
    {
      key: 'group',
      icon: <TeamOutlined />,
      label: '组管理',
    },
    {
      type: 'divider',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '系统设置',
    },
  ]

  // 处理菜单点击
  const handleMenuClick = ({ key }) => {
    navigate(`/ctc/${key}`)
  }

  return (
    <AntLayout className={styles.layout}>
      {/* 顶部导航栏 */}
      <Header className={styles.header}>
        <div className={styles.logo}>
          <span className={styles.logoText}>Z-CTC 统一用户中心</span>
        </div>
        <div className={styles.headerRight}>
          <Space size={24}>
            <Dropdown
              overlay={
                <div style={{
                  background: '#fff',
                  borderRadius: '8px',
                  boxShadow: '0 6px 16px rgba(0, 0, 0, 0.12)',
                  padding: '16px',
                  width: '360px'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '12px' }}>
                    <span style={{ fontSize: '14px', color: '#666' }}>租户域</span>
                    <Cascader
                      value={selectedTenant}
                      onChange={(value) => setSelectedTenant(value)}
                      options={tenantOptions}
                      placeholder="请选择租户域"
                      style={{ flex: 1 }}
                    />
                  </div>
                  <Divider style={{ margin: '12px 0' }} />
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <div style={{ display: 'flex', fontSize: '14px' }}>
                      <span style={{ color: '#999', width: '70px' }}>用户名：</span>
                      <span>{userInfo?.userName || '-'}</span>
                    </div>
                    <div style={{ display: 'flex', fontSize: '14px' }}>
                      <span style={{ color: '#999', width: '70px' }}>真实姓名：</span>
                      <span>{userInfo?.realName || '-'}</span>
                    </div>
                    <div style={{ display: 'flex', fontSize: '14px' }}>
                      <span style={{ color: '#999', width: '70px' }}>邮箱：</span>
                      <span>{userInfo?.email || '-'}</span>
                    </div>
                    <div style={{ display: 'flex', fontSize: '14px' }}>
                      <span style={{ color: '#999', width: '70px' }}>手机号：</span>
                      <span>{userInfo?.phone || '-'}</span>
                    </div>
                    <div style={{ display: 'flex', fontSize: '14px' }}>
                      <span style={{ color: '#999', width: '70px' }}>状态：</span>
                      <span>{userInfo?.status === 1 ? '正常' : '停用'}</span>
                    </div>
                  </div>
                  <Divider style={{ margin: '12px 0' }} />
                  <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '8px 12px', cursor: 'pointer', borderRadius: '4px', fontSize: '14px' }} onClick={() => {}}>
                      <UserOutlined /> 个人中心
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '8px 12px', cursor: 'pointer', borderRadius: '4px', fontSize: '14px' }} onClick={() => {}}>
                      <SettingOutlined /> 系统设置
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '8px 12px', cursor: 'pointer', borderRadius: '4px', fontSize: '14px' }} onClick={() => {
                      localStorage.removeItem('token')
                      localStorage.removeItem('userInfo')
                      navigate('/ctc/login')
                    }}>
                      <LogoutOutlined /> 退出登录
                    </div>
                  </div>
                </div>
              }
              placement="bottomRight"
              trigger={['click']}
            >
              <div className={styles.userInfo}>
                <Avatar icon={<UserOutlined />} size="small" />
                <Text className={styles.userName}>{userName}</Text>
                <DownOutlined />
              </div>
            </Dropdown>
          </Space>
        </div>
      </Header>

      <AntLayout className={styles.mainLayout}>
        {/* 侧边菜单 */}
        <Sider
          trigger={null}
          collapsible
          collapsed={collapsed}
          className={styles.sider}
          width={200}
        >
          <Menu
            mode="inline"
            selectedKeys={getSelectedKeys()}
            defaultOpenKeys={['overview', 'dashboard', 'user', 'role', 'permission', 'audit', 'tenant', 'domain', 'org', 'dept', 'group']}
            className={styles.menu}
            items={menuItems}
            onClick={handleMenuClick}
          />
          <div
            className={styles.collapsedBtn}
            onClick={() => setCollapsed(!collapsed)}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </div>
        </Sider>

        {/* 主内容区 */}
        <Content className={styles.content}>
          <Outlet />
        </Content>
      </AntLayout>
    </AntLayout>
  )
}

export default Layout