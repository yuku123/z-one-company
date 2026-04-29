import { Table, Button, Space, Tag, Card } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'

export default function UserList() {
  const columns = [
    {
      title: '用户ID',
      dataIndex: 'id',
      key: 'id',
      width: 80
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username'
    },
    {
      title: '姓名',
      dataIndex: 'realName',
      key: 'realName'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email'
    },
    {
      title: '角色',
      dataIndex: 'role',
      key: 'role',
      render: (role) => {
        const roleMap = {
          admin: { color: 'red', text: '管理员' },
          manager: { color: 'blue', text: '项目经理' },
          developer: { color: 'green', text: '开发人员' },
          guest: { color: 'default', text: '访客' }
        }
        const info = roleMap[role] || { color: 'default', text: role }
        return <Tag color={info.color}>{info.text}</Tag>
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === 'active' ? 'green' : 'red'}>
          {status === 'active' ? '启用' : '禁用'}
        </Tag>
      )
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
      username: 'admin',
      realName: '管理员',
      email: 'admin@example.com',
      role: 'admin',
      status: 'active',
      createTime: '2024-01-01'
    },
    {
      id: 2,
      username: 'zhangsan',
      realName: '张三',
      email: 'zhangsan@example.com',
      role: 'manager',
      status: 'active',
      createTime: '2024-01-02'
    },
    {
      id: 3,
      username: 'lisi',
      realName: '李四',
      email: 'lisi@example.com',
      role: 'developer',
      status: 'active',
      createTime: '2024-01-03'
    }
  ]

  return (
    <Card
      title="用户管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />}>
          新建用户
        </Button>
      }
    >
      <Table columns={columns} dataSource={data} rowKey="id" />
    </Card>
  )
}
