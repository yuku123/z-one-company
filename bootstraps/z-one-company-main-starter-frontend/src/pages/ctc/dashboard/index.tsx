import React from 'react';
import { Row, Col, Card, Statistic, List, Tag } from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  SafetyOutlined,
  FileTextOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';

const Dashboard: React.FC = () => {
  // 统计数据
  const statistics = [
    {
      title: '用户总数',
      value: 1234,
      icon: <UserOutlined />,
      color: '#1890ff',
      trend: 12,
    },
    {
      title: '角色总数',
      value: 56,
      icon: <TeamOutlined />,
      color: '#52c41a',
      trend: 5,
    },
    {
      title: '权限总数',
      value: 234,
      icon: <SafetyOutlined />,
      color: '#faad14',
      trend: -3,
    },
    {
      title: '审计日志',
      value: 5678,
      icon: <FileTextOutlined />,
      color: '#722ed1',
      trend: 23,
    },
  ];

  // 最近活动
  const recentActivities = [
    {
      id: 1,
      user: '张三',
      action: '登录系统',
      time: '2024-01-15 10:30:00',
      ip: '192.168.1.100',
    },
    {
      id: 2,
      user: '李四',
      action: '创建用户',
      time: '2024-01-15 10:25:00',
      ip: '192.168.1.101',
    },
    {
      id: 3,
      user: '王五',
      action: '修改角色权限',
      time: '2024-01-15 10:20:00',
      ip: '192.168.1.102',
    },
    {
      id: 4,
      user: '赵六',
      action: '登出系统',
      time: '2024-01-15 10:15:00',
      ip: '192.168.1.103',
    },
  ];

  return (
    <PageContainer title="概览" subTitle="4A + SSO 统一身份认证平台">
      {/* 统计卡片 */}
      <Row gutter={[16, 16]}>
        {statistics.map((stat, index) => (
          <Col xs={24} sm={12} lg={6} key={index}>
            <Card bordered={false}>
              <Statistic
                title={stat.title}
                value={stat.value}
                prefix={
                  <div
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      width: 40,
                      height: 40,
                      borderRadius: 8,
                      backgroundColor: stat.color,
                      color: '#fff',
                      marginRight: 12,
                    }}
                  >
                    {stat.icon}
                  </div>
                }
                suffix={
                  <span
                    style={{
                      color: stat.trend >= 0 ? '#52c41a' : '#ff4d4f',
                      fontSize: 14,
                      marginLeft: 8,
                    }}
                  >
                    {stat.trend >= 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
                    {Math.abs(stat.trend)}%
                  </span>
                }
              />
            </Card>
          </Col>
        ))}
      </Row>

      {/* 最近活动 */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={24}>
          <Card title="最近活动" bordered={false}>
            <List
              dataSource={recentActivities}
              renderItem={(item) => (
                <List.Item key={item.id}>
                  <List.Item.Meta
                    avatar={
                      <div
                        style={{
                          width: 40,
                          height: 40,
                          borderRadius: '50%',
                          backgroundColor: '#1890ff',
                          color: '#fff',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontSize: 16,
                          fontWeight: 'bold',
                        }}
                      >
                        {item.user.charAt(0)}
                      </div>
                    }
                    title={
                      <span>
                        <strong>{item.user}</strong>
                        <span style={{ margin: '0 8px' }}>·</span>
                        <span>{item.action}</span>
                      </span>
                    }
                    description={
                      <span>
                        <Tag size="small">IP: {item.ip}</Tag>
                        <span style={{ marginLeft: 8, color: '#999' }}>{item.time}</span>
                      </span>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Dashboard;
