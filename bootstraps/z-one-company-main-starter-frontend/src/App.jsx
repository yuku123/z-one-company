import { useState, useEffect } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu, Avatar, Dropdown, Space, Cascader, Divider } from 'antd'
import {
  AppstoreOutlined,
  SettingOutlined,
  FileTextOutlined,
  ApiOutlined,
  UserOutlined,
  ScheduleOutlined,
  CloudServerOutlined,
  DatabaseOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  AuditOutlined,
  TeamOutlined,
  SafetyOutlined,
  KeyOutlined,
  BookOutlined,
  UnorderedListOutlined,
  LoginOutlined,
  LogoutOutlined,
  DownOutlined,
} from '@ant-design/icons'
import { getCurrentUser, getTenantList } from './services/api'

const { Header, Sider, Content } = Layout

const menuItems = [
  {
    key: '/overview',
    icon: <AppstoreOutlined />,
    label: '概览',
  },
  {
    key: '/ctc',
    icon: <UserOutlined />,
    label: '4A中心',
    children: [
      { key: '/ctc/overview', label: '概览' },
      { key: '/ctc/dashboard', label: '工作台' },
      { key: '/ctc/user', label: '用户管理' },
      { key: '/ctc/role', label: '角色管理' },
      { key: '/ctc/permission', label: '权限管理' },
      { key: '/ctc/audit', label: '审计日志' },
      { key: '/ctc/tenant', label: '租户管理' },
      { key: '/ctc/domain', label: '域管理' },
      { key: '/ctc/org', label: '组织管理' },
      { key: '/ctc/dept', label: '部门管理' },
      { key: '/ctc/group', label: '组管理' },
    ]
  },
  {
    key: '/config',
    icon: <SettingOutlined />,
    label: '配置中心',
    children: [
      { key: '/config/list', label: '配置列表' },
      { key: '/config/dashboard', label: 'Dashboard' },
    ]
  },
  {
    key: '/task',
    icon: <FileTextOutlined />,
    label: '任务中心',
    children: [
      { key: '/task/dashboard', label: '仪表盘' },
      { key: '/task/task', label: '任务列表' },
      { key: '/task/project', label: '项目管理' },
      { key: '/task/user', label: '用户管理' },
    ]
  },
  {
    key: '/workflow',
    icon: <AuditOutlined />,
    label: '流程中心',
    children: [
      { key: '/workflow/dashboard', label: '仪表盘' },
      { key: '/workflow/todo', label: '待办任务' },
      { key: '/workflow/done', label: '已办任务' },
      { key: '/workflow/my-processes', label: '我的流程' },
    ]
  },
  {
    key: '/schedule',
    icon: <ScheduleOutlined />,
    label: '调度中心',
    children: [
      { key: '/schedule/dashboard', label: '仪表盘' },
      { key: '/schedule/job', label: '任务管理' },
    ]
  },
  {
    key: '/mist',
    icon: <CloudServerOutlined />,
    label: '密钥中心',
    children: [
      { key: '/mist/secret', label: '密钥管理' },
    ]
  },
  {
    key: '/meta',
    icon: <DatabaseOutlined />,
    label: '元数据',
    children: [
      { key: '/meta/dashboard', label: '仪表盘' },
      { key: '/meta/api', label: 'API管理' },
      { key: '/meta/application', label: '应用管理' },
      { key: '/meta/dict', label: '字典管理' },
      { key: '/meta/tenant', label: '租户管理' },
    ]
  },
]

function App() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const [userName, setUserName] = useState('管理员')
  const [userInfo, setUserInfo] = useState(null)
  const [tenantOptions, setTenantOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState([])

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/login')
      return
    }
    
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      try {
        const user = JSON.parse(userInfoStr)
        setUserName(user.userName || '管理员')
        setUserInfo(user)
        if (user.tenantId) {
          setSelectedTenant([user.tenantId])
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

    getTenantList().then(res => {
      if (res && Array.isArray(res) && res.length > 0) {
        const options = res.map(t => ({
          value: t.tenantCode,
          label: t.tenantName,
          children: [],
        }))
        setTenantOptions(options)
      }
    }).catch(() => {
      setTenantOptions([
        {
          value: 'tenant1',
          label: '租户1',
          children: [
            { value: 'domain1-1', label: '域1-1' },
            { value: 'domain1-2', label: '域1-2' },
          ],
        },
      ])
    })
  }, [])

  const handleMenuClick = ({ key, keyPath }) => {
    // 如果点击的是 CTC 登录，先检查是否已登录
    if (key === '/ctc/login') {
      const token = localStorage.getItem('token')
      if (token) {
        navigate('/ctc/overview')
      } else {
        navigate('/ctc/login')
      }
      return
    }
    if (keyPath && keyPath.length > 1) {
      navigate(key)
    } else if (!keyPath) {
      navigate(key)
    }
  }

  const getSelectedKeys = () => {
    const path = location.pathname
    return [path]
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          fontSize: collapsed ? 14 : 18,
          fontWeight: 'bold',
        }}>
          {collapsed ? 'OC' : 'One Company'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={getSelectedKeys()}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span onClick={() => setCollapsed(!collapsed)} style={{ fontSize: 18, cursor: 'pointer' }}>
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </span>
          <Space>
            <span>统一管理平台</span>
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
              <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} size="small" />
                <span style={{ marginLeft: 8 }}>{userName}</span>
                <DownOutlined style={{ marginLeft: 4 }} />
              </div>
            </Dropdown>
          </Space>
        </Header>
        <Content style={{ margin: 16, padding: 24, background: '#fff', minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default App