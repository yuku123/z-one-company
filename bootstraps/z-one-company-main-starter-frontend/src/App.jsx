import { useState, useEffect } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu, Avatar, Dropdown, Select } from 'antd'
import {
  AppstoreOutlined,
  SettingOutlined,
  FileTextOutlined,
  UserOutlined,
  ScheduleOutlined,
  CloudServerOutlined,
  DatabaseOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  AuditOutlined,
  DownOutlined,
  RobotOutlined,
  CodeOutlined,
  DeploymentUnitOutlined,
  ToolOutlined,
  CloudOutlined,
  DesktopOutlined,
  FolderOpenOutlined,
  BarChartOutlined,
  CalendarOutlined,
  GiftOutlined } from '@ant-design/icons'
import { getCurrentUser, getTenantList, getDomainByTenantCode } from './services/api'

const { Header, Sider, Content } = Layout

const menuItems = [
  { key: '/overview', icon: <AppstoreOutlined />, label: '概览' },
  { key: '/ctc', icon: <UserOutlined />, label: '4A中心', children: [
    { key: '/ctc/overview', label: '功能概览' },
    { key: '/ctc/tenant', label: '租户管理' },
    { key: '/ctc/org', label: '组织管理' },
    { key: '/ctc/user', label: '用户管理' },
    { key: '/ctc/permission', label: '权限管理' },
    { key: '/ctc/app', label: '应用管理' },
    { key: '/ctc/dict', label: '元典管理' },
    { key: '/ctc/role', label: '角色管理' },
    { key: '/ctc/audit', label: '审计日志' },
  ]},
  { key: '/config', icon: <SettingOutlined />, label: '配置中心', children: [
    { key: '/config/list', label: '配置列表' },
    { key: '/config/service', label: '服务列表' },
  ]},
  { key: '/task', icon: <FileTextOutlined />, label: '任务中心', children: [
    { key: '/task/dashboard', label: '仪表盘' },
    { key: '/task/task', label: '任务列表' },
    { key: '/task/project', label: '项目管理' },
    { key: '/task/user', label: '用户管理' },
  ]},
  { key: '/ai', icon: <RobotOutlined />, label: '智能中心', children: [
    { key: '/ai/mcp', label: 'MCP管理' },
    { key: '/ai/skill', label: 'SKILL管理' },
    { key: '/ai/agent', label: 'Agent应用' },
  ]},
  { key: '/rd', icon: <CodeOutlined />, label: '研发中心', children: [
    { key: '/rd/repo', label: '仓库中心' },
    { key: '/rd/sprint', label: '迭代管控' },
    { key: '/rd/ops', label: '运维中心' },
  ]},
  { key: '/resource', icon: <CloudOutlined />, label: '资源管理', children: [
    { key: '/resource/domain', label: '域名管理' },
    { key: '/resource/ecs', label: 'ECS管理' },
    { key: '/resource/rds', label: 'RDS管理' },
    { key: '/resource/oss', label: '对象存储' },
  ]},
  { key: '/ops', icon: <BarChartOutlined />, label: '运营管控', children: [
    { key: '/ops/channel', label: '渠道注册' },
    { key: '/ops/task', label: '任务排期' },
    { key: '/ops/delivery', label: '交付产物' },
  ]},
  { key: '/workflow', icon: <AuditOutlined />, label: '流程中心', children: [
    { key: '/workflow/dashboard', label: '仪表盘' },
    { key: '/workflow/todo', label: '待办任务' },
    { key: '/workflow/done', label: '已办任务' },
    { key: '/workflow/my-processes', label: '我的流程' },
  ]},
  { key: '/schedule', icon: <ScheduleOutlined />, label: '调度中心', children: [
    { key: '/schedule/dashboard', label: '仪表盘' },
    { key: '/schedule/job', label: '任务管理' },
  ]},
  { key: '/mist', icon: <CloudServerOutlined />, label: '密钥中心', children: [
    { key: '/mist/secret', label: '密钥管理' },
  ]},
  { key: '/meta', icon: <DatabaseOutlined />, label: '元数据', children: [
    { key: '/meta/dashboard', label: '仪表盘' },
    { key: '/meta/api', label: 'API管理' },
    { key: '/meta/application', label: '应用管理' },
    { key: '/meta/dict', label: '字典管理' },
    { key: '/meta/tenant', label: '租户管理' },
  ]},
]

function App() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const [userName, setUserName] = useState('管理员')
  const [userInfo, setUserInfo] = useState(null)
  const [tenantOptions, setTenantOptions] = useState([])
  const [domainOptions, setDomainOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(['default', ''])

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
        setTenantOptions(res.map(t => ({
          value: t.tenantCode,
          label: t.tenantName,
        })))
        // 默认加载第一个租户的域
        const first = res[0]
        setSelectedTenant([first.tenantCode, ''])
        localStorage.setItem('z_tenant', first.tenantCode || '')
        localStorage.setItem('z_domain', '')
        getDomainByTenantCode(first.tenantCode).then(domains => {
          if (domains && Array.isArray(domains)) {
            setDomainOptions(domains.map(d => ({ value: d.domainCode, label: d.domainName })))
          }
        }).catch(() => {})
      }
    }).catch(() => {})
  }, [])

  const handleMenuClick = ({ key }) => {
    if (key === '/ctc/login') {
      const token = localStorage.getItem('token')
      navigate(token ? '/ctc/overview' : '/ctc/login')
      return
    }
    navigate(key)
  }

  const handleTenantChange = (value) => {
    setSelectedTenant([value, ''])
    localStorage.setItem('z_tenant', value || '')
    localStorage.setItem('z_domain', '')
    setDomainOptions([])
    if (value) {
      getDomainByTenantCode(value).then(domains => {
        if (domains && Array.isArray(domains)) {
          setDomainOptions(domains.map(d => ({ value: d.domainCode, label: d.domainName })))
        }
      }).catch(() => {})
    }
  }

  const handleDomainChange = (value) => {
    setSelectedTenant([selectedTenant[0], value || ''])
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    navigate('/login')
  }

  const headerRight = (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      <Select
        value={selectedTenant[0]}
        onChange={handleTenantChange}
        options={tenantOptions}
        style={{ width: 140 }}
        placeholder="选择租户"
      />
      <Select
        value={selectedTenant[1] || undefined}
        onChange={handleDomainChange}
        options={domainOptions}
        style={{ width: 140 }}
        placeholder="选择域"
        allowClear
      />
      <Dropdown menu={{
        items: [
          { key: 'profile', label: '个人中心' },
          { key: 'settings', label: '系统设置' },
          { type: 'divider' },
          { key: 'logout', label: '退出登录', danger: true },
        ],
        onClick: ({ key }) => { if (key === 'logout') handleLogout() },
      }} placement="bottomRight" trigger={['click']}>
        <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
          <Avatar icon={<UserOutlined />} size="small" />
          <span style={{ marginLeft: 8 }}>{userName}</span>
          <DownOutlined style={{ marginLeft: 4 }} />
        </div>
      </Dropdown>
    </div>
  )

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div style={{
          height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'white', fontSize: collapsed ? 14 : 18, fontWeight: 'bold',
        }}>
          {collapsed ? 'OC' : 'One Company'}
        </div>
        <Menu theme="dark" mode="inline" selectedKeys={[location.pathname]}
          items={menuItems} onClick={handleMenuClick} />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span onClick={() => setCollapsed(!collapsed)} style={{ fontSize: 18, cursor: 'pointer' }}>
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </span>
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <span>统一管理平台</span>
          </div>
          {headerRight}
        </Header>
        <Content style={{ margin: 16, padding: 24, background: '#fff', minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default App
