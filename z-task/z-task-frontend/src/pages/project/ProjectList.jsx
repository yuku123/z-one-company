import { Table, Button, Space, Tag, Card } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'

export default function ProjectList() {
  const columns = [
    {
      title: '项目ID',
      dataIndex: 'id',
      key: 'id',
      width: 80
    },
    {
      title: '项目名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '项目描述',
      dataIndex: 'description',
      key: 'description'
    },
    {
      title: '负责人',
      dataIndex: 'manager',
      key: 'manager'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const statusMap = {
          planning: { color: 'gold', text: '规划中' },
          active: { color: 'green', text: '进行中' },
          completed: { color: 'blue', text: '已完成' },
          archived: { color: 'default', text: '已归档' }
        }
        const info = statusMap[status] || { color: 'default', text: status }
        return <Tag color={info.color}>{info.text}</Tag>
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime'
    },
    {
      title: '操作',
      key: 'action',
      render: () => (
        <Space size="middle">
          <Button type="link" icon={<EditOutlined />}>
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
      name: 'Z-Task项目',
      description: '任务管理系统开发',
      manager: '张三',
      status: 'active',
      createTime: '2024-01-01'
    },
    {
      id: 2,
      name: 'Z-Config项目',
      description: '配置中心系统开发',
      manager: '李四',
      status: 'active',
      createTime: '2024-02-01'
    }
  ]

  return (
    <Card
      title="项目管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />}>
          新建项目
        </Button>
      }
    >
      <Table columns={columns} dataSource={data} rowKey="id" />
    </Card>
  )
}
