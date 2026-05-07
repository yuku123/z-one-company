import { Card } from 'antd'
import { ToolOutlined } from '@ant-design/icons'
const Page = () => (
  <Card title="运维中心" style={{ height: 'calc(100vh - 180px)' }}>
    <div style={{ textAlign: 'center', marginTop: 80, color: '#999' }}>
      <ToolOutlined style={{ fontSize: 64, display: 'block', marginBottom: 16 }} />运维中心 - 开发中</div>
  </Card>
)
export default Page
