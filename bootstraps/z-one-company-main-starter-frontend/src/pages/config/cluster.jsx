import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Modal, Form, Input, message, Popconfirm, Tag } from 'antd'
import { PlusOutlined, ReloadOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { configApi } from '@/services/api'

const ClusterPage = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [modalOpen, setModalOpen] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [form] = Form.useForm()

  const fetchList = async () => {
    setLoading(true)
    try {
      const res = await configApi.clusterList()
      if (res.success) {
        setData((res.data || []).map((item) => ({ ...item, key: item.id })))
      }
    } catch (e) {
      message.error('获取集群列表失败')
    } finally { setLoading(false) }
  }

  const handleSave = async (values) => {
    try {
      const res = await configApi.clusterSave({ ...values, id: editingRecord?.id })
      if (res.success) {
        message.success(editingRecord ? '更新成功' : '创建成功')
        setModalOpen(false)
        setEditingRecord(null)
        form.resetFields()
        fetchList()
      } else {
        message.error(res.message || '操作失败')
      }
    } catch (e) {
      message.error('网络错误')
    }
  }

  const handleDelete = async (record) => {
    try {
      const res = await configApi.clusterDelete(record.id)
      if (res.success) {
        message.success('删除成功')
        fetchList()
      } else {
        message.error(res.message || '删除失败')
      }
    } catch (e) {
      message.error('网络错误')
    }
  }

  const openEdit = (record) => {
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalOpen(true)
  }

  const openAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalOpen(true)
  }

  useEffect(() => { fetchList() }, [])

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '命名空间名称', dataIndex: 'name', key: 'name' },
    { title: '访问地址', dataIndex: 'address', key: 'address', ellipsis: true },
    { title: '端口', dataIndex: 'port', key: 'port', width: 80 },
    { title: '状态', dataIndex: 'isDeleted', key: 'isDeleted', width: 100,
      render: (v) => <Tag color={v ? 'red' : 'green'}>{v ? '已删除' : '正常'}</Tag> },
    { title: '操作', key: 'action', width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除" description={`确定要删除 "${record.name}" 吗？`}
            onConfirm={() => handleDelete(record)} okText="确定" cancelText="取消">
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Card title="命名空间管理"
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={fetchList} loading={loading}>刷新</Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>新建</Button>
          </Space>
        }>
        <Table columns={columns} dataSource={data} loading={loading}
          pagination={{ showTotal: (t) => `共 ${t} 条` }} />
      </Card>

      <Modal title={editingRecord ? '编辑命名空间' : '新建命名空间'}
        open={modalOpen} onCancel={() => { setModalOpen(false); setEditingRecord(null) }}
        onOk={() => form.submit()} destroyOnClose>
        <Form form={form} layout="vertical" onFinish={handleSave}>
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请输入名称' }]}>
            <Input placeholder="如：DEFAULT_NAMESPACE" />
          </Form.Item>
          <Form.Item name="address" label="访问地址">
            <Input placeholder="如：127.0.0.1" />
          </Form.Item>
          <Form.Item name="port" label="端口">
            <Input placeholder="如：8848" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ClusterPage
