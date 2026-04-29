import React from 'react'
import { Card, Typography, Space, Row, Col, Statistic } from 'antd'
import {
  TeamOutlined,
  SafetyOutlined,
  KeyOutlined,
  AuditOutlined,
} from '@ant-design/icons'

const { Title, Text, Paragraph } = Typography

const Overview = () => {
  return (
    <div style={{ padding: '0' }}>
      {/* 概览标题 */}
      <div style={{ marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>Z-CTC 统一用户中心</Title>
        <Text type="secondary">Z-CTC (Zero - Central Token Center) 统一用户认证与权限管理平台</Text>
      </div>

      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="租户数量"
              value={1}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="用户总数"
              value={5}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="角色数量"
              value={2}
              prefix={<SafetyOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="权限数量"
              value={15}
              prefix={<KeyOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {/* 平台介绍 */}
      <Card title="关于 Z-CTC 统一用户中心" style={{ marginBottom: 24 }}>
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          <div>
            <Title level={5}>平台定位</Title>
            <Paragraph>
              Z-CTC (Zero - Central Token Center) 是为一人公司打造的全栈统一用户认证与权限管理平台。
              采用 Spring Boot 微服务架构，支持多租户管理，提供完善的用户、角色、权限管理功能。
            </Paragraph>
          </div>

          <div>
            <Title level={5}>核心功能</Title>
            <ul>
              <li>多租户支持 - 支持多个独立租户，租户间数据完全隔离</li>
              <li>用户管理 - 支持用户名、手机号、邮箱等多种登录方式</li>
              <li>角色权限 - 基于 RBAC 模型的细粒度权限控制</li>
              <li>单点登录 - 支持 Token 和 Session 两种认证方式</li>
              <li>审计日志 - 完整的操作日志记录，支持追溯查询</li>
            </ul>
          </div>

          <div>
            <Title level={5}>技术栈</Title>
            <ul>
              <li>后端: Spring Boot 2.7 + MyBatis Plus + MySQL</li>
              <li>前端: React + Ant Design + Vite</li>
              <li>认证: JWT Token</li>
              <li>部署: Docker + Nginx</li>
            </ul>
          </div>

          <div>
            <Title level={5}>当前租户信息</Title>
            <ul>
              <li><strong>租户名称:</strong> 一人公司</li>
              <li><strong>租户编码:</strong> MAIN</li>
              <li><strong>管理员:</strong> admin</li>
              <li><strong>状态:</strong> 正常</li>
            </ul>
          </div>
        </Space>
      </Card>

      {/* 快速入口 */}
      <Row gutter={16}>
        <Col span={8}>
          <Card hoverable style={{ textAlign: 'center' }}>
            <TeamOutlined style={{ fontSize: 32, color: '#1890ff' }} />
            <div style={{ marginTop: 8 }}>用户管理</div>
          </Card>
        </Col>
        <Col span={8}>
          <Card hoverable style={{ textAlign: 'center' }}>
            <SafetyOutlined style={{ fontSize: 32, color: '#52c41a' }} />
            <div style={{ marginTop: 8 }}>角色管理</div>
          </Card>
        </Col>
        <Col span={8}>
          <Card hoverable style={{ textAlign: 'center' }}>
            <KeyOutlined style={{ fontSize: 32, color: '#faad14' }} />
            <div style={{ marginTop: 8 }}>权限管理</div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Overview