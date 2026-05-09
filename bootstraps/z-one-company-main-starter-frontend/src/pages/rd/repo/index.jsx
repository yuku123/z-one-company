import { Card, Tabs } from 'antd'
import ImageRepo from './ImageRepo'
import ImageTag from './ImageTag'
import ImageBuild from './ImageBuild'

const RepoCenter = () => {
  const items = [
    { key: 'repo', label: '镜像仓库', children: <ImageRepo /> },
    { key: 'tag', label: '镜像版本', children: <ImageTag /> },
    { key: 'build', label: '构建记录', children: <ImageBuild /> },
  ]

  return (
    <Card title="仓库中心" style={{ height: 'calc(100vh - 180px)' }}>
      <Tabs defaultActiveKey="repo" items={items} />
    </Card>
  )
}

export default RepoCenter