import React, { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import {
  Layout as AntLayout,
  Menu,
  Avatar,
  Dropdown,
  Badge,
  Space,
  Typography,
} from 'antd';
import {
  DashboardOutlined,
  CheckCircleOutlined,
  HistoryOutlined,
  FileTextOutlined,
  BranchesOutlined,
  SettingOutlined,
  BellOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
} from '@ant-design/icons';
import styles from './index.module.css';

const { Header, Sider, Content } = AntLayout;
const { Text } = Typography;

const Layout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  // 获取当前选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname;
    if (path.startsWith('/approval-center/todo')) return ['todo'];
    if (path.startsWith('/approval-center/done')) return ['done'];
    if (path.startsWith('/approval-center/my-processes')) return ['my-processes'];
    if (path.startsWith('/designer')) return ['designer'];
    return ['dashboard'];
  };

  // 用户菜单项
  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人中心',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '系统设置',
    },
    {
      type: 'divider' as const,
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
  ];

  // 处理用户菜单点击
  const handleUserMenuClick = ({ key }: { key: string }) => {
    if (key === 'logout') {
      // 处理登出逻辑
      localStorage.removeItem('token');
      navigate('/login');
    }
  };

  return (
    <AntLayout className={styles.layout}>
      {/* 顶部导航栏 */}
      <Header className={styles.header}>
        <div className={styles.logo}>
          <BranchesOutlined className={styles.logoIcon} />
          <span className={styles.logoText}>Z-WF 工作流</span>
        </div>
        <div className={styles.headerRight}>
          <Space size={24}>
            <Badge count={5} size="small">
              <BellOutlined className={styles.icon} />
            </Badge>
            <Dropdown
              menu={{ items: userMenuItems, onClick: handleUserMenuClick }}
              placement="bottomRight"
            >
              <div className={styles.userInfo}>
                <Avatar icon={<UserOutlined />} size="small" />
                <Text className={styles.userName}>管理员</Text>
                <DownOutlined className={styles.downIcon} />
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
          width={220}
        >
          <Menu
            mode="inline"
            selectedKeys={getSelectedKeys()}
            className={styles.menu}
            items={[
              {
                key: 'dashboard',
                icon: <DashboardOutlined />,
                label: <Link to="/">工作台</Link>,
              },
              {
                key: 'approval-center',
                icon: <CheckCircleOutlined />,
                label: '审批中心',
                children: [
                  {
                    key: 'todo',
                    label: <Link to="/approval-center/todo">待办任务</Link>,
                  },
                  {
                    key: 'done',
                    label: <Link to="/approval-center/done">已办任务</Link>,
                  },
                  {
                    key: 'my-processes',
                    label: <Link to="/approval-center/my-processes">我发起的</Link>,
                  },
                ],
              },
              {
                key: 'designer',
                icon: <BranchesOutlined />,
                label: <Link to="/designer">流程设计</Link>,
              },
              {
                key: 'monitor',
                icon: <HistoryOutlined />,
                label: '流程监控',
                children: [
                  {
                    key: 'running',
                    label: '运行中流程',
                  },
                  {
                    key: 'completed',
                    label: '已完成流程',
                  },
                ],
              },
              {
                key: 'settings',
                icon: <SettingOutlined />,
                label: '系统设置',
              },
            ]}
          />
          <div
            className={styles.collapsedBtn}
            onClick={() => setCollapsed(!collapsed)}
          >
            {collapsed ? '>' : '<'}
          </div>
        </Sider>

        {/* 主内容区 */}
        <Content className={styles.content}>
          <Outlet />
        </Content>
      </AntLayout>
    </AntLayout>
  );
};

export default Layout;
