import { RocketOutlined, RobotOutlined, BarChartOutlined, SettingOutlined, AppstoreOutlined, DollarOutlined } from '@ant-design/icons'
import { Card, Row, Col, Typography } from 'antd'
import { Link, Outlet, useNavigate } from 'umi'

const { Title, Text } = Typography

const AiIndex = () => {
  const navigate = useNavigate()

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <Title level={2}>AI 智能中心</Title>
        <Text type="secondary">Agent 应用 · LLM 模型管理 · 用量统计</Text>
      </div>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/ai/agent/app')} style={{ height: 120 }}>
            <RobotOutlined style={{ fontSize: 36, color: '#1890ff' }} />
            <Title level={5} style={{ marginTop: 12, marginBottom: 0 }}>Agent 应用</Title>
            <Text type="secondary" style={{ fontSize: 12 }}>创建和管理 Agent 应用</Text>
          </Card>
        </Col>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/ai/llm/provider')} style={{ height: 120 }}>
            <SettingOutlined style={{ fontSize: 36, color: '#52c41a' }} />
            <Title level={5} style={{ marginTop: 12, marginBottom: 0 }}>LLM 模型</Title>
            <Text type="secondary" style={{ fontSize: 12 }}>管理 LLM 提供商和模型</Text>
          </Card>
        </Col>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/ai/usage')} style={{ height: 120 }}>
            <BarChartOutlined style={{ fontSize: 36, color: '#faad14' }} />
            <Title level={5} style={{ marginTop: 12, marginBottom: 0 }}>用量统计</Title>
            <Text type="secondary" style={{ fontSize: 12 }}>Token 消耗和费用明细</Text>
          </Card>
        </Col>
        <Col span={6}>
          <Card hoverable onClick={() => navigate('/ai/agent/config')} style={{ height: 120 }}>
            <AppstoreOutlined style={{ fontSize: 36, color: '#722ed1' }} />
            <Title level={5} style={{ marginTop: 12, marginBottom: 0 }}>应用配置</Title>
            <Text type="secondary" style={{ fontSize: 12 }}>工具和技能配置</Text>
          </Card>
        </Col>
      </Row>

      {/* 子路由内容 */}
      <Outlet />
    </div>
  )
}

export default AiIndex
