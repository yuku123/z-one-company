import { Table, Tag, Card } from 'antd'

// z-task 的用户来自 z-ctc SSO 同步，用户管理在 z-ctc 统一进行
// 这里只展示同步过来的用户列表（只读）
export default function UserList() {
  const columns = [
    {
      title: 'ID',
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
      title: '昵称',
      dataIndex: 'nickname',
      key: 'nickname',
      render: (text) => text || '-'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '来源',
      dataIndex: 'source',
      key: 'source',
      render: (text) => text || '-'
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt'
    }
  ]

  return (
    <Card title="用户管理">
      <Table
        columns={columns}
        dataSource={[]}
        rowKey="id"
        locale={{ emptyText: '暂无数据（用户由 z-ctc 统一管理）' }}
      />
    </Card>
  )
}
