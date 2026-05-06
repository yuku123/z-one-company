import React, { useState, useEffect } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import {
  Layout as AntLayout,
  Menu,
  Avatar,
  Dropdown,
  Badge,
  Space,
  Typography,
  Select,
  message,
} from 'antd';
import {
  DashboardOutlined,
  CheckCircleOutlined,
  HistoryOutlined,
  BranchesOutlined,
  SettingOutlined,
  BellOutlined,
  UserOutlined,
  LogoutOutlined,
  DownOutlined,
  SwapOutlined,
} from '@ant-design/icons';
import { authRequest } from '../../services/request';
import styles from './index.module.css';

const { Header, Sider, Content } = AntLayout;
const { Text } = Typography;

interface Tenant {
  id: number;
  name: string;
  code: string;
}

const Layout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [currentTenant, setCurrentTenant] = useState<string>('');
  const [switching, setSwitching] = useState(false);

  // 加载当前 token 中的租户信息
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        setCurrentTenant(payload.tenantId || '');
      } catch {}
    }
  }, []);

  // 加载用户所属租户列表
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;
    authRequest.get('/tenant/list', {
      headers: { Authorization: `Bearer ${token}` },
    }).then((res: any) => {
      if (res.success && res.data) {
        setTenants(res.data);
      }
    }).catch(() => {});
  }, []);

  // 切换租户
  const handleTenantChange = async (tenantId: string) => {
    const token = localStorage.getItem('token');
    if (!token) return;
    setSwitching(true);
    try {
      const res: any = await authRequest.post('/auth/refresh',
        { tenantId },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (res.success && res.data?.token) {
        localStorage.setItem('token', res.data.token);
        setCurrentTenant(tenantId);
        message.success('租户切换成功');
        window.location.reload();
      } else {
        message.error(res.message || '切换失败');
      }
    } catch (e: any) {
      message.error(e?.response?.data?.message || '切换失败');
    } finally {
      setSwitching(false);
    }
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
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      navigate('/login');
    }
  };

  const tenantLabel = tenants.find(t => String(t.id) === currentTenant)?.name
    || tenants.find(t => t.code === currentTenant)?.name
    || currentTenant
    || '默认租户';

  // 获取当前选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname;
    if (path.startsWith('/approval-center/todo')) return ['todo'];
    if (path.startsWith('/approval-center/done')) return ['done'];
    if (path.startsWith('/approval-center/my-processes')) return ['my-processes'];
    if (path.startsWith('/designer')) return ['designer'];
    return ['dashboard'];
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
            {/* 租户切换 */}
            {tenants.length > 0 && (
              <Select
                value={currentTenant}
                onChange={handleTenantChange}
                loading={switching}
                suffixIcon={<SwapOutlined />}
                style={{ minWidth: 140 }}
                placeholder="选择租户"
                options={tenants.map(t => ({ value: t.code, label: t.name }))}
              />
            )}

            <Badge count={5} size="small">
              <BellOutlined className={styles.icon} />
            </Badge>
            <Dropdown
              menu={{ items: userMenuItems, onClick: handleUserMenuClick }}
              placement="bottomRight"
            >
              <div className={styles.userInfo}>
                <Avatar icon={<UserOutlined />} size="small" />
                <Text className={styles.userName}>{tenantLabel}</Text>
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
