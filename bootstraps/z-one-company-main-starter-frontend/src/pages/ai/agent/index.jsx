import { useState, useEffect, useRef } from 'react'
import {
  Card, Table, Button, Space, Modal, Form, Input, Select, message, Tag, Popconfirm,
  Row, Col, InputNumber, Switch, Divider, Badge, Statistic, Empty, Tabs, List, Avatar, Tooltip, QRCode
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined, RocketOutlined, ShareAltOutlined,
  PlayCircleOutlined, CopyOutlined, SettingOutlined, EyeOutlined, CodeOutlined,
  RobotOutlined, GlobalOutlined, LockOutlined, HistoryOutlined, BarChartOutlined,
  LinkOutlined, CheckCircleFilled, CloseCircleFilled, LoadingOutlined, SendOutlined
} from '@ant-design/icons'
import { agentApi } from '../../../services/api'

const { TextArea } = Input
const { Option } = Select
const { TabPane } = Tabs

// ===== 应用列表页 =====
const AgentAppPage = () => {
  const [activeTab, setActiveTab] = useState('list')
  return (
    <Card style={{ minHeight: 'calc(100vh - 140px)' }}>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab={<span><BarChartOutlined /> 应用管理</span>} key="list">
          <AppListPage />
        </TabPane>
        <TabPane tab={<span><SettingOutlined /> 工具配置</span>} key="tools">
          <ToolConfigPage />
        </TabPane>
        <TabPane tab={<span><GlobalOutlined /> 分享管理</span>} key="shares">
          <ShareManagementPage />
        </TabPane>
      </Tabs>
    </Card>
  )
}

// ===== 应用列表 =====
const AppListPage = () => {
  const [dataSource, setDataSource] = useState([])
  const [loading, setLoading] = useState(false)
  const [searchKey, setSearchKey] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [pageNum, setPageNum] = useState(1)
  const [pageSize] = useState(12)
  const [total, setTotal] = useState(0)
  const [modalVisible, setModalVisible] = useState(false)
  const [form] = Form.useForm()
  const [editingApp, setEditingApp] = useState(null)
  const [shareModalVisible, setShareModalVisible] = useState(false)
  const [selectedInstance, setSelectedInstance] = useState(null)
  const [shareLinks, setShareLinks] = useState([])
  const [createLoading, setCreateLoading] = useState(false)
  const [instances, setInstances] = useState({})

  useEffect(() => { fetchData() }, [pageNum, searchKey, statusFilter])

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await agentApi.appPage({ appName: searchKey, status: statusFilter, id: pageNum })
      if (res.data) {
        setDataSource(res.data.records || [])
        setTotal(res.data.total || 0)
      }
    } catch (e) {
      message.error('加载失败: ' + (e.message || ''))
    }
    setLoading(false)
  }

  const handleCreate = () => {
    setEditingApp(null)
    form.resetFields()
    form.setFieldsValue({
      modelProvider: 'ollama',
      modelName: 'qwen2.5:7b',
      status: 'DRAFT',
    })
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingApp(record)
    let draftData = null
    // 尝试获取草稿
    agentApi.appDraftGet(record.appCode).then(draftRes => {
      if (draftRes.data) {
        try {
          draftData = JSON.parse(draftRes.data)
        } catch (e) {}
      }
    }).finally(() => {
      form.setFieldsValue({
        appCode: draftData?.appCode || record.appCode,
        appName: draftData?.appName || record.appName,
        description: draftData?.description || record.description,
        prompt: draftData?.prompt || record.prompt,
        modelName: draftData?.modelName || record.modelName || 'qwen2.5:7b',
        modelProvider: draftData?.modelProvider || record.modelProvider || 'ollama',
        iconUrl: draftData?.iconUrl || record.iconUrl,
      })
      setModalVisible(true)
    })
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setCreateLoading(true)
      if (editingApp) {
        await agentApi.appUpdate({ id: editingApp.id, ...values })
        message.success('更新成功')
      } else {
        await agentApi.appCreate(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      fetchData()
    } catch (e) {
      if (!e.errorFields) message.error('操作失败')
    } finally {
      setCreateLoading(false)
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

  const handleShare = async (record) => {
    let instance = instances[record.appCode]
    if (!instance) {
      try {
        const res = await agentApi.instanceCreate({
          appCode: record.appCode,
          userId: 'admin',
          userName: '管理员',
        })
        instance = res.data
        setInstances(prev => ({ ...prev, [record.appCode]: instance }))
      } catch (e) {
        message.error('创建实例失败')
        return
      }
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
    navigator.clipboard.writeText(link).then(() => message.success('链接已复制到剪贴板')).catch(() => {
      // fallback
      const textarea = document.createElement('textarea')
      textarea.value = link
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      message.success('链接已复制')
    })
  }

  const columns = [
    {
      title: '应用',
      key: 'app',
      width: 260,
      render: (_, record) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Avatar shape="square" size={40} src={record.iconUrl} icon={<RobotOutlined />} style={{ background: '#1890ff' }} />
          <div>
            <div style={{ fontWeight: 500 }}>{record.appName}</div>
            <div style={{ fontSize: 12, color: '#999' }}>{record.appCode}</div>
          </div>
        </div>
      )
    },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (val) => (
        <Badge status={val === 'PUBLISHED' ? 'success' : 'warning'} text={val === 'PUBLISHED' ? '已发布' : '草稿'} />
      )
    },
    {
      title: '模型',
      key: 'model',
      width: 140,
      render: (_, record) => (
        <span>
          <Tag color="blue">{record.modelProvider || 'ollama'}</Tag>
          <span style={{ fontSize: 12 }}>{record.modelName || 'qwen2.5:7b'}</span>
        </span>
      )
    },
    {
      title: '操作',
      width: 300,
      render: (_, record) => (
        <Space size="small" wrap>
          <Tooltip title="编辑配置"><Button size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button></Tooltip>
          {record.status === 'DRAFT' && (
            <Tooltip title="发布后即可分享"><Button size="small" type="primary" icon={<RocketOutlined />} onClick={() => handlePublish(record.appCode)}>发布</Button></Tooltip>
          )}
          <Tooltip title="创建独立实例"><Button size="small" icon={<PlayCircleOutlined />} onClick={() => handleShare(record)}>实例</Button></Tooltip>
          <Tooltip title="生成分享链接"><Button size="small" icon={<ShareAltOutlined />} onClick={() => handleShare(record)}>分享</Button></Tooltip>
          <Popconfirm title="确定删除此应用?" onConfirm={() => handleDelete(record.id)} okText="删除" okButtonProps={{ danger: true }}>
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      )
    }
  ]

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16, gap: 12, flexWrap: 'wrap' }}>
        <Space>
          <Input.Search placeholder="搜索应用名称/编码" onSearch={v => { setSearchKey(v); setPageNum(1) }} style={{ width: 220 }} allowClear />
          <Select placeholder="状态筛选" allowClear style={{ width: 120 }} onChange={v => { setStatusFilter(v); setPageNum(1) }}>
            <Option value="DRAFT">草稿</Option>
            <Option value="PUBLISHED">已发布</Option>
          </Select>
        </Space>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新建应用</Button>
      </div>

      <Table
        dataSource={dataSource}
        columns={columns}
        loading={loading}
        rowKey="id"
        pagination={{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: false,
          showTotal: t => `共 ${t} 个应用`,
          onChange: p => setPageNum(p),
        }}
        locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="还没有应用，点击新建开始创建" /> }}
      />

      {/* 创建/编辑 Modal */}
      <Modal
        title={editingApp ? `编辑应用: ${editingApp.appName}` : '新建应用'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={720}
        confirmLoading={createLoading}
        okText={editingApp ? '保存' : '创建'}
      >
        <Form form={form} layout="vertical" size="middle">
          <Row gutter={16}>
            <Col span={16}>
              <Form.Item name="appName" label="应用名称" rules={[{ required: true, message: '请输入应用名称' }]}>
                <Input placeholder="如：智能客服助手" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="iconUrl" label="图标URL">
                <Input placeholder="可选" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="description" label="应用描述">
            <TextArea rows={2} placeholder="简短描述这个应用的能力和使用场景" />
          </Form.Item>

          <Divider orientation="left">模型配置</Divider>
          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="modelProvider" label="提供商" rules={[{ required: true }]} initialValue="ollama">
                <Select>
                  <Option value="ollama">Ollama (本地)</Option>
                  <Option value="openai">OpenAI</Option>
                  <Option value="azure">Azure OpenAI</Option>
                  <Option value="dashscope">阿里通义</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={16}>
              <Form.Item name="modelName" label="模型名称" rules={[{ required: true }]} initialValue="qwen2.5:7b">
                <Input placeholder="如: qwen2.5:7b, gpt-4o-mini" />
              </Form.Item>
            </Col>
          </Row>

          <Divider orientation="left">角色设定</Divider>
          <Form.Item name="prompt" label="系统提示词" rules={[{ required: true, message: '请定义Agent的角色' }]}
            extra="定义Agent的身份、能力、行为规范和约束条件，越详细越能获得稳定的回复">
            <TextArea rows={6} placeholder={`你是公司的智能客服助手，负责：\n1. 回答用户关于产品的问题\n2. 帮助用户解决问题\n3. 当无法回答时，引导用户联系人工\n\n注意：请使用友好的语气，保持专业。`} />
          </Form.Item>

          {!editingApp && (
            <Form.Item name="status" label="创建后状态" initialValue="DRAFT">
              <Select>
                <Option value="DRAFT">草稿（仅自己可见）</Option>
                <Option value="PUBLISHED">直接发布</Option>
              </Select>
            </Form.Item>
          )}
        </Form>
      </Modal>

      {/* 分享 Modal */}
      <Modal
        title="分享应用"
        open={shareModalVisible}
        onCancel={() => setShareModalVisible(false)}
        footer={null}
        width={560}
      >
        {selectedInstance && (
          <div>
            <Card size="small" style={{ marginBottom: 16, background: '#f5f5f5' }}>
              <Row gutter={16}>
                <Col span={12}>
                  <Statistic title="实例编码" value={selectedInstance.instanceCode} valueStyle={{ fontSize: 14 }} />
                </Col>
                <Col span={12}>
                  <Statistic title="状态" value={selectedInstance.status === 'ACTIVE' ? '活跃' : selectedInstance.status} valueStyle={{ fontSize: 14 }} />
                </Col>
                <Col span={12}>
                  <Statistic title="访问次数" value={selectedInstance.visitCount || 0} valueStyle={{ fontSize: 14 }} />
                </Col>
                <Col span={12}>
                  <Statistic title="最后访问" value={selectedInstance.lastVisitTime || '暂无'} valueStyle={{ fontSize: 14 }} />
                </Col>
              </Row>
            </Card>

            <Button type="primary" icon={<LinkOutlined />} onClick={handleGenerateLink} block style={{ marginBottom: 16 }}>
              生成新分享链接
            </Button>

            <List
              header={<b>已有分享链接 ({shareLinks.length})</b>}
              bordered
              dataSource={shareLinks}
              locale={{ emptyText: '暂无分享链接，点击上方按钮生成' }}
              renderItem={(link) => (
                <List.Item
                  actions={[
                    <Button key="copy" size="small" icon={<CopyOutlined />} onClick={() => copyShareLink(link.shareCode)}>复制</Button>,
                    <Tag key="status" color={link.status === 'ACTIVE' ? 'green' : 'red'}>
                      {link.status === 'ACTIVE' ? '有效' : link.status === 'DISABLED' ? '已禁用' : '已过期'}
                    </Tag>
                  ]}
                >
                  <List.Item.Meta
                    avatar={<Avatar icon={link.status === 'ACTIVE' ? <CheckCircleFilled /> : <CloseCircleFilled />} style={{ background: link.status === 'ACTIVE' ? '#52c41a' : '#ff4d4f' }} />}
                    title={<span style={{ fontFamily: 'monospace' }}>{window.location.origin}/share/{link.shareCode}</span>}
                    description={`访问次数: ${link.visitCount || 0} | ${link.gmtCreate || ''}`}
                  />
                </List.Item>
              )}
            />

            <Divider />

            <div style={{ textAlign: 'center' }}>
              <QRCode value={`${window.location.origin}/share/${shareLinks[0]?.shareCode || ''}`} size={160} />
              <div style={{ marginTop: 8, color: '#666', fontSize: 12 }}>
                扫码打开分享页面
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  )
}

// ===== 工具配置页（占位，可扩展） =====
const ToolConfigPage = () => (
  <Card>
    <Empty description="工具配置功能开发中" image={Empty.PRESENTED_IMAGE_SIMPLE} />
    <div style={{ marginTop: 16, color: '#999', fontSize: 13 }}>
      <p>可配置项：</p>
      <ul>
        <li> MCP 工具绑定（连接外部工具服务）</li>
        <li> Skill 技能关联（绑定已有技能）</li>
        <li> 知识库关联（向量数据库）</li>
        <li> 自定义变量定义</li>
        <li> 函数调用（Function Calling）配置</li>
      </ul>
    </div>
  </Card>
)

// ===== 分享管理页 =====
const ShareManagementPage = () => {
  const [shares, setShares] = useState([])
  const [loading, setLoading] = useState(false)

  // 暂时显示空状态
  return (
    <Card>
      <Empty description="分享管理功能开发中" image={Empty.PRESENTED_IMAGE_SIMPLE} />
    </Card>
  )
}

export default AgentAppPage
