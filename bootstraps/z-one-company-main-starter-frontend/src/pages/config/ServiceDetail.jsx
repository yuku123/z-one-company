import { useState, useEffect } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { Card, Table, Tag, Button, Space, message, Descriptions } from 'antd'
import { ArrowLeftOutlined, ReloadOutlined } from '@ant-design/icons'
import { namingApi } from '@/services/api'

const ServiceDetail = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const serviceName = searchParams.get('serviceName') || ''
  const [loading, setLoading] = useState(false)
  const [instances, setInstances] = useState([])

  const fetchInstances = async () => {
    setLoading(true)
    try {
      const res = await namingApi.getInstances(serviceName)
      if (res.success && res.data) {
        setInstances((res.data || []).map((item, i) => ({ key: item.instanceId || i, ...item })))
      } else {
        message.error(res.message || '获取实例失败')
      }
    } catch (e) {
      message.error('网络错误')
    } finally { setLoading(false) }
  }

  useEffect(() => { if (serviceName) fetchInstances() }, [serviceName])

  const healthyCount = instances.filter(i => i.healthy).length

  const columns = [
    { title: '实例ID', dataIndex: 'instanceId', key: 'instanceId', ellipsis: true },
    { title: 'IP', dataIndex: 'ip', key: 'ip' },
    { title: '端口', dataIndex: 'port', key: 'port' },
    { title: '集群', dataIndex: 'clusterName', key: 'clusterName' },
    { title: '权重', dataIndex: 'weight', key: 'weight' },
    { title: '健康状态', dataIndex: 'healthy', key: 'healthy',
      render: (h) => <Tag color={h ? 'success' : 'error'}>{h ? '健康' : '异常'}</Tag> },
    { title: '元数据', dataIndex: 'metadata', key: 'metadata', ellipsis: true,
      render: (m) => typeof m === 'object' ? JSON.stringify(m) : (m || '-') },
  ]

  return (
    <div>
      <Card size="small" style={{ marginBottom: 16 }}>
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/config/service')}>返回</Button>
          <span style={{ fontSize: 16, fontWeight: 600 }}>{serviceName}</span>
        </Space>
      </Card>
      <Card size="small" style={{ marginBottom: 16 }}>
        <Descriptions size="small" column={4}>
          <Descriptions.Item label="总实例">{instances.length}</Descriptions.Item>
          <Descriptions.Item label="健康实例"><Tag color="success">{healthyCount}</Tag></Descriptions.Item>
        </Descriptions>
      </Card>
      <Card title="实例列表"
        extra={<Button icon={<ReloadOutlined />} onClick={fetchInstances} loading={loading}>刷新</Button>}>
        <Table columns={columns} dataSource={instances} loading={loading}
          pagination={{ pageSize: 10, showTotal: (t) => `共 ${t} 条` }} />
      </Card>
    </div>
  )
}

export default ServiceDetail
