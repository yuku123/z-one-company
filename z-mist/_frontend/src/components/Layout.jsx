import { Outlet, useNavigate } from 'react-router-dom'
import { Layout as AntLayout, Menu, Avatar, Dropdown } from 'antd'
import {
  KeyOutlined,
  LogoutOutlined,
  UserOutlined,
} from '@ant-design/icons'

const { Header, Sider, Content } = AntLayout

function Layout() {
  const navigate = useNavigate()

  const menuItems = [
    {
      key: 'secret',
      icon: <KeyOutlined />,
      label: '密钥管理',
      onClick: () => navigate('/secret'),
    },
  ]

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: () => {
        localStorage.removeItem('token')
        navigate('/login')
      },
    },
  ]

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <AntLayout>
        <Sider theme="dark" width={200}>
          <div style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontSize: 18,
            fontWeight: 'bold'
          }}>
            z-mist
          </div>
          <Menu
            theme="dark"
            mode="inline"
            defaultSelectedKeys={['secret']}
            items={menuItems}
            style={{ height: '100%', borderRight: 0 }}
          />
        </Sider>
        <AntLayout>
          <Header style={{
            padding: '0 24px',
            background: '#fff',
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center'
          }}>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Avatar icon={<UserOutlined />} style={{ cursor: 'pointer' }} />
            </Dropdown>
          </Header>
          <Content style={{ margin: '16px', padding: 24, background: '#fff' }}>
            <Outlet />
          </Content>
        </AntLayout>
      </AntLayout>
    </AntLayout>
  )
}

export default Layout