import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Modal, Form, Input, Select, message, Tag, Popconfirm, Row, Col } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, RocketOutlined, ShareAltOutlined, PlayCircleOutlined, CopyOutlined } from '@ant-design/icons'
import { agentApi } from '../../../services/api'

const { TextArea } = Input
const { Option } = Select

const AgentAppPage = () => {
  const [dataSource, setDataSource] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [form] = Form.useForm()
  const [editingApp, setEditingApp] = useState(null)
  const [instances, setInstances] = useState({})
  const [instanceModalVisible, setInstanceModalVisible] = useState(false)
  const [shareModalVisible, setShareModalVisible] = useState(false)
  const [selectedInstance, setSelectedInstance] = useState(null)
  const [shareLinks, setShareLinks] = useState([])

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await agentApi.appPage({ appName: '', status: '' })
      if (res.data) {
        setDataSource(res.data.records || [])
      }
    } catch (e) {
      message.error('加载失败')
    }
    setLoading(false)
  }

  const handleCreate = () => {
    setEditingApp(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingApp(record)
    form.setFieldsValue({
      appCode: record.appCode,
      appName: record.appName,
      description: record.description,
      prompt: record.prompt,
      modelName: record.modelName || 'qwen2.5:7b',
      modelProvider: record.modelProvider || 'ollama',
    })
    setModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingApp) {
        await agentApi.appUpdate({ ...editingApp, ...values })
        message.success('更新成功')
      } else {
        await agentApi.appCreate(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      fetchData()
    } catch (e) {
      message.error('操作失败')
    }
  }

  const handleDelete = async (id) => {
    try {
      await agentApi.appDelete(id)
      message.success('删除成功')
      fetchData()
    } catch (e) {
      message.error('删除失败')
    }
  }

  const handlePublish = async (appCode) => {
    try {
      await agentApi.appPublish(appCode)
      message.success('发布成功')
      fetchData()
    } catch (e) {
      message.error('发布失败')
    }
  }

  const handleCreateInstance = async (record) => {
    try {
      const res = await agentApi.instanceCreate({
        appCode: record.appCode,
        userId: 'user_001',
        userName: '测试用户',
      })
      if (res.data) {
        setSelectedInstance(res.data)
        setInstances(prev => ({ ...prev, [record.appCode]: res.data }))
        message.success('实例创建成功')
        fetchShareLinks(res.data.instanceCode)
      }
    } catch (e) {
      message.error('创建实例失败')
    }
  }

  const handleShare = async (record) => {
    let instance = instances[record.appCode]
    if (!instance) {
      const res = await agentApi.instanceCreate({
        appCode: record.appCode,
        userId: 'user_001',
        userName: '测试用户',
      })
      instance = res.data
      setInstances(prev => ({ ...prev, [record.appCode]: instance }))
    }
    setSelectedInstance(instance)
    fetchShareLinks(instance.instanceCode)
    setShareModalVisible(true)
  }

  const fetchShareLinks = async (instanceCode) => {
    try {
      const res = await agentApi.shareList(instanceCode)
      setShareLinks(res.data || [])
    } catch (e) {}
  }

  const handleGenerateLink = async () => {
    if (!selectedInstance) return
    try {
      const res = await agentApi.shareCreate({
        instanceCode: selectedInstance.instanceCode,
        appCode: selectedInstance.appCode,
      })
      if (res.data) {
        setShareLinks(prev => [...prev, res.data])
        message.success('分享链接已生成')
      }
    } catch (e) {
      message.error('生成失败')
    }
  }

  const copyShareLink = (shareCode) => {
    const link = `${window.location.origin}/share/${shareCode}`
    navigator.clipboard.writeText(link)
    message.success('链接已复制')
  }

  const columns = [
    { title: '应用名称', dataIndex: 'appName', width: 150 },
    { title: '编码', dataIndex: 'appCode', width: 150 },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (val) => (
        <Tag color={val === 'PUBLISHED' ? 'green' : 'orange'}>
          {val === 'PUBLISHED' ? '已发布' : '草稿'}
        </Tag>
      )
    },
    { title: '模型', dataIndex: 'modelName', width: 120 },
    {
      title: '操作',
      width: 250,
      render: (_, record) => (
        <Space size="small">
          <Button size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          {record.status === 'DRAFT' && (
            <Button size="small" type="primary" icon={<RocketOutlined />} onClick={() => handlePublish(record.appCode)}>发布</Button>
          )}
          <Button size="small" icon={<PlayCircleOutlined />} onClick={() => handleCreateInstance(record)}>创建实例</Button>
          <Button size="small" icon={<ShareAltOutlined />} onClick={() => handleShare(record)}>分享</Button>
          <Popconfirm title="确定删除?" onConfirm={() => handleDelete(record.id)}>
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      )
    }
  ]

  return (
    <Card title="Agent 应用" extra={<Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新建应用</Button>}>
      <Table dataSource={dataSource} columns={columns} loading={loading} rowKey="id" pagination={{ pageSize: 10 }} />

      {/* 创建/编辑 Modal */}
      <Modal title={editingApp ? '编辑应用' : '新建应用'} open={modalVisible} onOk={handleSubmit} onCancel={() => setModalVisible(false)} width={700}>
        <Form form={form} layout="vertical">
          <Form.Item name="appName" label="应用名称" rules={[{ required: true }]}>
            <Input placeholder="输入应用名称" />
          </Form.Item>
          <Form.Item name="description" label="应用描述">
            <TextArea rows={2} placeholder="简短描述" />
          </Form.Item>
          <Form.Item name="prompt" label="系统提示词" rules={[{ required: true }]}>
            <TextArea rows={4} placeholder="定义Agent的角色、能力和行为规范" />
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="modelProvider" label="模型提供商" initialValue="ollama">
                <Select>
                  <Option value="ollama">Ollama</Option>
                  <Option value="openai">OpenAI</Option>
                  <Option value="azure">Azure</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="modelName" label="模型名称" initialValue="qwen2.5:7b">
                <Input placeholder="如: qwen2.5:7b" />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>

      {/* 分享 Modal */}
      <Modal title="分享应用" open={shareModalVisible} onCancel={() => setShareModalVisible(false)} footer={null} width={500}>
        {selectedInstance && (
          <div>
            <p style={{ marginBottom: 16 }}>实例: <b>{selectedInstance.instanceName}</b></p>
            <Button type="primary" icon={<ShareAltOutlined />} onClick={handleGenerateLink} style={{ marginBottom: 16 }}>
              生成新分享链接
            </Button>
            <div>
              <h4>已有分享链接:</h4>
              {shareLinks.length === 0 ? <p style={{ color: '#999' }}>暂无分享链接</p> : shareLinks.map(link => (
                <Card key={link.shareCode} size="small" style={{ marginBottom: 8 }}>
                  <Space>
                    <span>{window.location.origin}/share/{link.shareCode}</span>
                    <Button size="small" icon={<CopyOutlined />} onClick={() => copyShareLink(link.shareCode)}>复制</Button>
                    <Tag color={link.status === 'ACTIVE' ? 'green' : 'red'}>{link.status === 'ACTIVE' ? '有效' : '失效'}</Tag>
                  </Space>
                </Card>
              ))}
            </div>
          </div>
        )}
      </Modal>
    </Card>
  )
}

export default AgentAppPage
