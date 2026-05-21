import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, message, Tag } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ToolOutlined } from '@ant-design/icons'
import { request } from 'umi'

const { confirm } = Modal

const AgentAppList = () => {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingApp, setEditingApp] = useState(null)
  const [form] = Form.useForm()

  const fetchApps = async () => {
    setLoading(true)
    try {
      const res = await request('/api/agent/app/list', { method: 'GET' })
      setData(res.data || [])
    } catch (e) {
      message.error('加载失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchApps() }, [])

  const handleAdd = () => {
    setEditingApp(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingApp(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (record) => {
    confirm({
      title: '确认删除',
      content: `删除应用 ${record.appName} ？`,
      onOk: async () => {
        await request('/api/agent/app/delete', { method: 'POST', data: { id: record.id } })
        message.success('删除成功')
        fetchApps()
      }
    })
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      await request(editingApp ? '/api/agent/app/update' : '/api/agent/app/create', {
        method: 'POST',
        data: values
      })
      message.success(editingApp ? '更新成功' : '创建成功')
      setModalVisible(false)
      fetchApps()
    } catch (e) {
      // 表单验证失败
    }
  }

  const columns = [
    { title: '应用编码', dataIndex: 'appCode', width: 120 },
    { title: '应用名称', dataIndex: 'appName', width: 150 },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: v => <Tag color={v === 'ENABLE' ? 'green' : 'red'}>{v === 'ENABLE' ? '启用' : '禁用'}</Tag>
    },
    { title: '模型', dataIndex: 'modelCode', width: 120 },
    {
      title: '操作', width: 180,
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<ToolOutlined />} onClick={() => request('/api/agent/app/config', { method: 'GET', params: { appCode: record.appCode } })}>配置</Button>
          <Button size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Button size="small" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>删除</Button>
        </Space>
      )
    }
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新建应用</Button>
      </div>

      <Table columns={columns} dataSource={data} loading={loading} rowKey="id" />

      <Modal
        title={editingApp ? '编辑应用' : '新建应用'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="appCode" label="应用编码" rules={[{ required: true }]}>
            <Input disabled={!!editingApp} />
          </Form.Item>
          <Form.Item name="appName" label="应用名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="modelCode" label="默认模型" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="qwen2.5">Qwen 2.5</Select.Option>
              <Select.Option value="claude-3.5">Claude 3.5</Select.Option>
              <Select.Option value="gpt-4o">GPT-4o</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue="ENABLE">
            <Select>
              <Select.Option value="ENABLE">启用</Select.Option>
              <Select.Option value="DISABLE">禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default AgentAppList
