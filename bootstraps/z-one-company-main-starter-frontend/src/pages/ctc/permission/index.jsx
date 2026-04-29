import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, message, Popconfirm, Modal, Form, Select, Tree } from 'antd'
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons'
import { getPermissionList, createPermission, updatePermission, deletePermission } from '../../../services/api'

const PermissionManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  })
  const [searchText, setSearchText] = useState('')
  const [modalVisible, setModalVisible] = useState(false)
  const [editingPermission, setEditingPermission] = useState(null)
  const [form] = Form.useForm()

  const fetchPermissionList = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res = await getPermissionList({
        pageNum: page,
        pageSize: pageSize,
        keyword: searchText || undefined,
      })
      setData(res.data?.records || [])
      setPagination({
        current: res.data?.current || 1,
        pageSize: pageSize,
        total: res.data?.total || 0,
      })
    } catch (error) {
      message.error('获取权限列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPermissionList()
  }, [])

  const handleSearch = () => {
    fetchPermissionList(1, pagination.pageSize)
  }

  const handleTableChange = (newPagination) => {
    fetchPermissionList(newPagination.current, newPagination.pageSize)
  }

  const handleAdd = () => {
    setEditingPermission(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingPermission(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await deletePermission(id)
      message.success('删除成功')
      fetchPermissionList(pagination.current, pagination.pageSize)
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingPermission) {
        await updatePermission(editingPermission.id, values)
        message.success('更新成功')
      } else {
        await createPermission(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      fetchPermissionList(pagination.current, pagination.pageSize)
    } catch (error) {
      message.error(error.response?.data?.message || '操作失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: '权限名称',
      dataIndex: 'permissionName',
      key: 'permissionName',
    },
    {
      title: '权限编码',
      dataIndex: 'permissionCode',
      key: 'permissionCode',
    },
    {
      title: '权限类型',
      dataIndex: 'permissionType',
      key: 'permissionType',
      render: (type) => {
        const colorMap = { 'menu': 'blue', 'button': 'green', 'api': 'orange' }
        return <Tag color={colorMap[type] || 'default'}>{type}</Tag>
      },
    },
    {
      title: '父级ID',
      dataIndex: 'parentId',
      key: 'parentId',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={status === 1 ? 'success' : 'default'}>
          {status === 1 ? '正常' : '停用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确认删除" description="确定要删除该权限吗？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <Card
      title="权限管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增权限
        </Button>
      }
    >
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Input.Search
            placeholder="搜索权限名称/编码"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onSearch={handleSearch}
            style={{ width: 300 }}
            allowClear
          />
          <Button icon={<ReloadOutlined />} onClick={() => fetchPermissionList(pagination.current, pagination.pageSize)}>
            刷新
          </Button>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        pagination={{
          ...pagination,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        loading={loading}
        onChange={handleTableChange}
        rowKey="id"
      />

      <Modal
        title={editingPermission ? '编辑权限' : '新增权限'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="permissionName" label="权限名称" rules={[{ required: true, message: '请输入权限名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="permissionCode" label="权限编码" rules={[{ required: true, message: '请输入权限编码' }]}>
            <Input disabled={!!editingPermission} />
          </Form.Item>
          <Form.Item name="permissionType" label="权限类型" rules={[{ required: true, message: '请选择权限类型' }]}>
            <Select>
              <Select.Option value="menu">菜单</Select.Option>
              <Select.Option value="button">按钮</Select.Option>
              <Select.Option value="api">API</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="parentId" label="父级ID">
            <Input type="number" />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>停用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

export default PermissionManagement
