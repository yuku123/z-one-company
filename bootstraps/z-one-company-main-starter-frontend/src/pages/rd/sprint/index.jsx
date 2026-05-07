import { Card } from 'antd'
import { DeploymentUnitOutlined } from '@ant-design/icons'
const Page = () => (
  <Card title="迭代管控" style={{ height: 'calc(100vh - 180px)' }}>
    <div style={{ textAlign: 'center', marginTop: 80, color: '#999' }}>
      <DeploymentUnitOutlined style={{ fontSize: 64, display: 'block', marginBottom: 16 }} />迭代管控 - 开发中</div>
  </Card>
)
export default Page
