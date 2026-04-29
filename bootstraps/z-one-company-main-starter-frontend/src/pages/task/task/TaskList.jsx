import { Table, Button, Space, Tag, Input, Select, Card } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Search } = Input
const { Option } = Select

export default function TaskList() {
  const navigate = useNavigate()

  const columns = [
    {
      title: '任务ID',
      dataIndex: 'id',
      key: 'id',
      width: 80
    },
    {
      title: '任务名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '所属项目',
      dataIndex: 'project',
      key: 'project'
    },
    {
      title: '负责人',
      dataIndex: 'assignee',
      key: 'assignee'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const statusMap = {
          pending: { color: 'gold', text: '待开始' },
          processing: { color: 'blue', text: '进行中' },
          completed: { color: 'green', text: '已完成' },
          cancelled: { color: 'red', text: '已取消' }
        }
        const info = statusMap[status] || { color: 'default', text: status }
        return <Tag color={info.color}>{info.text}</Tag>
      }
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      render: (priority) => {
        const priorityMap = {
          low: { color: 'green', text: '低' },
          medium: { color: 'gold', text: '中' },
          high: { color: 'orange', text: '高' },
          urgent: { color: 'red', text: '紧急' }
        }
        const info = priorityMap[priority] || { color: 'default', text: priority }
        return <Tag color={info.color}>{info.text}</Tag>
      }
    },
    {
      title: '截止日期',
      dataIndex: 'deadline',
      key: 'deadline'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />} onClick={() => navigate(`/task/edit/${record.id}`)}>
            编辑
          </Button>
          <Button type="link" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Space>
      )
    }
  ]

  // 模拟数据
  const data = [
    {
      id: 1,
      name: '完成前端页面开发',
      project: 'Z-Task项目',
      assignee: '张三',
      status: 'processing',
      priority: 'high',
      deadline: '2024-04-15'
    },
    {
      id: 2,
      name: '编写后端接口',
      project: 'Z-Task项目',
      assignee: '李四',
      status: 'pending',
      priority: 'medium',
      deadline: '2024-04-20'
    }
  ]

  return (
    <Card
      title="任务管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/task/edit/new')}>
          新建任务
        </Button>
      }
    >
      <div style={{ marginBottom: 16, display: 'flex', gap: 16 }}>
        <Search
          placeholder="搜索任务名称"
          allowClear
          enterButton={<SearchOutlined />}
          style={{ width: 300 }}
        />
        <Select placeholder="状态" style={{ width: 120 }} allowClear>
          <Option value="pending">待开始</Option>
          <Option value="processing">进行中</Option>
          <Option value="completed">已完成</Option>
          <Option value="cancelled">已取消</Option>
        </Select>
        <Select placeholder="优先级" style={{ width: 120 }} allowClear>
          <Option value="low">低</Option>
          <Option value="medium">中</Option>
          <Option value="high">高</Option>
          <Option value="urgent">紧急</Option>
        </Select>
      </div>
      <Table columns={columns} dataSource={data} rowKey="id" />
    </Card>
  )
}
