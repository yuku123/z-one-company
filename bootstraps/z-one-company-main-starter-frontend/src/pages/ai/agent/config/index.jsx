import { useState, useEffect } from 'react'
import { useParams, useSearchParams } from 'umi'
import { Card, Tabs, Table, Button, Space, Modal, Form, Input, Select, message, Tag, Switch, Divider, List, Typography } from 'antd'
import { PlusOutlined, DeleteOutlined, SaveOutlined } from '@ant-design/icons'
import { request } from 'umi'

const { Title, Text } = Typography
const { TabPane } = Tabs

const AgentAppConfig = () => {
  const { appCode } = useParams()
  const [searchParams] = useSearchParams()
  const appCodeParam = appCode || searchParams.get('appCode')

  const [appConfig, setAppConfig] = useState(null)
  const [tools, setTools] = useState([])
  const [skills, setSkills] = useState([])
  const [toolTemplates, setToolTemplates] = useState([])
  const [skillTemplates, setSkillTemplates] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [modalType, setModalType] = useState('tool') // tool | skill
  const [form] = Form.useForm()

  const fetchAppConfig = async () => {
    if (!appCodeParam) return
    setLoading(true)
    try {
      const res = await request('/api/agent/app/config', { method: 'GET', params: { appCode: appCodeParam } })
      setAppConfig(res.data?.app || null)
      setTools(res.data?.tools || [])
      setSkills(res.data?.skills || [])
    } catch (e) {
      message.error('加载失败')
    } finally {
      setLoading(false)
    }
  }

  const fetchTemplates = async () => {
    try {
      const [toolRes, skillRes] = await Promise.all([
        request('/api/llm-center/tool-template/list', { method: 'GET' }),
        request('/api/llm-center/skill-template/list', { method: 'GET' })
      ])
      setToolTemplates(toolRes.data || [])
      setSkillTemplates(skillRes.data || [])
    } catch (e) {
      console.error('加载模板失败', e)
    }
  }

  useEffect(() => {
    fetchAppConfig()
    fetchTemplates()
  }, [appCodeParam])

  const openModal = (type) => {
    setModalType(type)
    form.resetFields()
    setModalVisible(true)
  }

  const addTool = async () => {
    try {
      const values = await form.validateFields()
      await request('/api/agent/app/tool/add', {
        method: 'POST',
        data: { appCode: appCodeParam, ...values }
      })
      message.success('添加工具成功')
      setModalVisible(false)
      fetchAppConfig()
    } catch (e) {}
  }

  const removeTool = async (toolId) => {
    await request('/api/agent/app/tool/remove', {
      method: 'POST',
      data: { appCode: appCodeParam, toolId }
    })
    message.success('移除工具成功')
    fetchAppConfig()
  }

  const addSkill = async () => {
    try {
      const values = await form.validateFields()
      await request('/api/agent/app/skill/add', {
        method: 'POST',
        data: { appCode: appCodeParam, ...values }
      })
      message.success('添加技能成功')
      setModalVisible(false)
      fetchAppConfig()
    } catch (e) {}
  }

  const removeSkill = async (skillId) => {
    await request('/api/agent/app/skill/remove', {
      method: 'POST',
      data: { appCode: appCodeParam, skillId }
    })
    message.success('移除技能成功')
    fetchAppConfig()
  }

  const toggleTool = async (toolId, enabled) => {
    await request('/api/agent/app/tool/toggle', {
      method: 'POST',
      data: { appCode: appCodeParam, toolId, enabled }
    })
    fetchAppConfig()
  }

  const toolColumns = [
    { title: '工具名称', dataIndex: 'toolName' },
    { title: '工具类型', dataIndex: 'toolType', render: v => <Tag>{v}</Tag> },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    {
      title: '启用', dataIndex: 'enabled',
      render: (v, record) => <Switch checked={v} onChange={(checked) => toggleTool(record.id, checked)} />
    },
    {
      title: '操作',
      render: (_, record) => (
        <Button size="small" danger icon={<DeleteOutlined />} onClick={() => removeTool(record.id)}>移除</Button>
      )
    }
  ]

  const skillColumns = [
    { title: '技能名称', dataIndex: 'skillName' },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    { title: '提示词', dataIndex: 'prompt', ellipsis: true },
    {
      title: '操作',
      render: (_, record) => (
        <Button size="small" danger icon={<DeleteOutlined />} onClick={() => removeSkill(record.id)}>移除</Button>
      )
    }
  ]

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Title level={4}>{appConfig?.appName || appCodeParam}</Title>
        <Text type="secondary">{appConfig?.description}</Text>
        <Divider />
        <Space>
          <Text>应用编码: <strong>{appCodeParam}</strong></Text>
          <Text>模型: <Tag>{appConfig?.modelCode || '-'}</Tag></Text>
          <Text>状态: <Tag color={appConfig?.status === 'ENABLE' ? 'green' : 'red'}>{appConfig?.status === 'ENABLE' ? '启用' : '禁用'}</Tag></Text>
        </Space>
      </Card>

      <Tabs defaultActiveKey="tools">
        <TabPane tab="工具配置" key="tools">
          <Card
            title="已配置工具"
            extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => openModal('tool')}>添加工具</Button>}
          >
            <Table columns={toolColumns} dataSource={tools} rowKey="id" loading={loading} />
          </Card>
        </TabPane>

        <TabPane tab="技能配置" key="skills">
          <Card
            title="已配置技能"
            extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => openModal('skill')}>添加技能</Button>}
          >
            <Table columns={skillColumns} dataSource={skills} rowKey="id" loading={loading} />
          </Card>
        </TabPane>

        <TabPane tab="提示词配置" key="prompt">
          <Card title="系统提示词">
            <Form.Item label="System Prompt">
              <Input.TextArea rows={10} placeholder="定义 Agent 的角色和行为..." />
            </Form.Item>
            <Button type="primary" icon={<SaveOutlined />}>保存提示词</Button>
          </Card>
        </TabPane>
      </Tabs>

      <Modal
        title={modalType === 'tool' ? '添加工具' : '添加技能'}
        open={modalVisible}
        onOk={modalType === 'tool' ? addTool : addSkill}
        onCancel={() => setModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          {modalType === 'tool' ? (
            <>
              <Form.Item name="templateId" label="工具模板" rules={[{ required: true }]}>
                <Select placeholder="选择工具模板">
                  {toolTemplates.map(t => (
                    <Select.Option key={t.id} value={t.id}>{t.toolName} - {t.description}</Select.Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="enabled" label="默认启用" valuePropName="checked" initialValue={true}>
                <Switch />
              </Form.Item>
            </>
          ) : (
            <>
              <Form.Item name="templateId" label="技能模板" rules={[{ required: true }]}>
                <Select placeholder="选择技能模板">
                  {skillTemplates.map(s => (
                    <Select.Option key={s.id} value={s.id}>{s.skillName} - {s.description}</Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  )
}

export default AgentAppConfig
