import { useState, useEffect } from 'react'
import { Card, Table, Tag, Button, Space, message } from 'antd'
import { ReloadOutlined, EyeOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { namingApi } from '@/services/api'

const ServiceList = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])

  const fetchServiceList = async () => {
    setLoading(true)
    try {
      const res = await namingApi.listServices()
      if (res.success && res.data) {
        setData(res.data.map((item, i) => ({ key: i + 1, ...item })))
      } else {
        message.error(res.message || '获取服务列表失败')
      }
    } catch (e) {
      message.error('网络错误')
    } finally { setLoading(false) }
  }

  useEffect(() => { fetchServiceList() }, [])

  const columns = [
    { title: '服务名', dataIndex: 'serviceName', key: 'serviceName' },
    { title: '分组', dataIndex: 'groupName', key: 'groupName' },
    { title: '健康实例', dataIndex: 'healthyInstanceCount', key: 'healthyInstanceCount',
      render: (c) => <Tag color="success">{c || 0}</Tag> },
    { title: '总实例', dataIndex: 'totalInstanceCount', key: 'totalInstanceCount' },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (s) => <Tag color={s === 'UP' || s === '健康' ? 'success' : 'error'}>{s || '未知'}</Tag> },
    { title: '操作', key: 'action',
      render: (_, record) => (
        <Button type="link" icon={<EyeOutlined />}
          onClick={() => navigate(`/config/service/detail?serviceName=${encodeURIComponent(record.serviceName)}`)}>
          详情
        </Button>
      ),
    },
  ]

  return (
    <Card title="服务列表"
      extra={<Button icon={<ReloadOutlined />} onClick={fetchServiceList} loading={loading}>刷新</Button>}>
      <Table columns={columns} dataSource={data} loading={loading}
        pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (t) => `共 ${t} 条` }} />
    </Card>
  )
}

export default ServiceList
