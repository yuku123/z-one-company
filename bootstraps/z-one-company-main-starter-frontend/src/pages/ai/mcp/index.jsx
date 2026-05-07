import { Card } from 'antd'
import { ApiOutlined } from '@ant-design/icons'

const McpPage = () => (
  <Card title="MCP 管理" style={{ height: 'calc(100vh - 180px)' }}>
    <div style={{ textAlign: 'center', marginTop: 80, color: '#999' }}>
      <ApiOutlined style={{ fontSize: 64, display: 'block', marginBottom: 16 }} />
      MCP 管理 - 开发中
    </div>
  </Card>
)
export default McpPage
