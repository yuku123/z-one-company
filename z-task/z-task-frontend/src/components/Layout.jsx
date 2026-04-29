import { Layout, Menu } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  DashboardOutlined,
  FileTextOutlined,
  ProjectOutlined,
  UserOutlined,
  LogoutOutlined
} from '@ant-design/icons'

const { Header, Sider, Content } = Layout

export default function AppLayout() {
  const navigate = useNavigate()
  const location = useLocation()

  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: '仪表盘'
    },
    {
      key: '/task',
      icon: <FileTextOutlined />,
      label: '任务管理'
    },
    {
      key: '/project',
      icon: <ProjectOutlined />,
      label: '项目管理'
    },
    {
      key: '/user',
      icon: <UserOutlined />,
      label: '用户管理'
    }
  ]

  const handleLogout = () => {
    localStorage.removeItem('token')
    navigate('/login')
  }

  return (
    <Layout style={{ height: '100%' }}>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ color: 'white', fontSize: '20px', fontWeight: 'bold' }}>
          Z-Task 任务管理系统
        </div>
        <div style={{ color: 'white', cursor: 'pointer' }} onClick={handleLogout}>
          <LogoutOutlined /> 退出登录
        </div>
      </Header>
      <Layout>
        <Sider width={200} theme="light">
          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
            items={menuItems}
            onClick={({ key }) => navigate(key)}
            style={{ height: '100%', borderRight: 0 }}
          />
        </Sider>
        <Layout style={{ padding: '24px' }}>
          <Content
            style={{
              background: '#fff',
              padding: 24,
              margin: 0,
              minHeight: 280,
            }}
          >
            <Outlet />
          </Content>
        </Layout>
      </Layout>
    </Layout>
  )
}
