import { Card } from 'antd'
import { RobotOutlined } from '@ant-design/icons'

const AgentPage = () => (
  <Card title="Agent 应用" style={{ height: 'calc(100vh - 180px)' }}>
    <div style={{ textAlign: 'center', marginTop: 80, color: '#999' }}>
      <RobotOutlined style={{ fontSize: 64, display: 'block', marginBottom: 16 }} />
      Agent 应用 - 开发中
    </div>
  </Card>
)
export default AgentPage
