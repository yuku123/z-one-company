import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams, useLocation } from 'react-router-dom'
import { Card, Form, Input, Button, message, Switch, Space, Select } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import axios from 'axios'

const ConfigEdit = () => {
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [isEdit, setIsEdit] = useState(false)
  const [searchParams] = useSearchParams()
  const location = useLocation()
  const [namespaceList, setNamespaceList] = useState([])

  // 获取命名空间列表
  useEffect(() => {
    const fetchNamespaceList = async () => {
      try {
        const res = await axios.get('/api/cluster/list')
        setNamespaceList(res.data.map(item => ({ label: item.name, value: item.name })))
      } catch (e) {
        console.error('获取命名空间失败', e)
      }
    }
    fetchNamespaceList()
  }, [])

  // 获取 URL 参数中的配置信息
  useEffect(() => {
    const state = location.state
    if (state && state.config) {
      // 编辑模式
      setIsEdit(true)
      form.setFieldsValue({
        dataId: state.config.dataId,
        group: state.config.group,
        content: state.config.content,
        appName: state.config.appName,
        namespace: state.config.namespace,
        configDesc: state.config.configDesc,
      })
    } else {
      // 新建模式，从 query 参数获取初始值
      const dataId = searchParams.get('dataId')
      const group = searchParams.get('group')
      const namespace = searchParams.get('namespace')
      if (dataId) form.setFieldsValue({ dataId })
      if (group) form.setFieldsValue({ group })
      if (namespace) form.setFieldsValue({ namespace })
      else form.setFieldsValue({ namespace: 'DEFAULT_NAMESPACE' })
    }
  }, [location.state, searchParams, form])

  const onFinish = async (values) => {
    setLoading(true)
    try {
      const res = await axios.post('/api/config/saveConfig', values)
      if (res.data.success) {
        message.success(isEdit ? '修改成功' : '保存成功')
        navigate('/config/list')
      } else {
        message.error(res.data.message || (isEdit ? '修改失败' : '保存失败'))
      }
    } catch (error) {
      console.error('保存错误:', error)
      message.error('网络错误')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card
      title={
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/config/list')}>
            返回
          </Button>
          <span>{isEdit ? '编辑配置' : '新建配置'}</span>
        </Space>
      }
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        style={{ maxWidth: 800 }}
      >
        <Form.Item
          name="namespace"
          label="命名空间"
          rules={[{ required: true, message: '请选择命名空间' }]}
        >
          <Select placeholder="请选择命名空间" options={namespaceList} />
        </Form.Item>

        <Form.Item
          name="appName"
          label="应用名"
        >
          <Input placeholder="请输入应用名（可选）" />
        </Form.Item>

        <Form.Item
          name="dataId"
          label="Data ID"
          rules={[{ required: true, message: '请输入 Data ID' }]}
        >
          <Input disabled={isEdit} placeholder="请输入 Data ID，如：application.yml" />
        </Form.Item>

        <Form.Item
          name="group"
          label="Group"
          rules={[{ required: true, message: '请输入 Group' }]}
          initialValue="DEFAULT_GROUP"
        >
          <Input disabled={isEdit} placeholder="请输入 Group" />
        </Form.Item>

        <Form.Item
          name="content"
          label="配置内容"
          rules={[{ required: true, message: '请输入配置内容' }]}
        >
          <Input.TextArea
            rows={15}
            placeholder="请输入配置内容..."
            style={{ fontFamily: 'Monaco, Consolas, monospace' }}
          />
        </Form.Item>

        <Form.Item
          name="configDesc"
          label="描述"
        >
          <Input.TextArea rows={3} placeholder="请输入配置描述（可选）" />
        </Form.Item>

        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              {isEdit ? '保存修改' : '保存'}
            </Button>
            <Button onClick={() => navigate('/config/list')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  )
}

export default ConfigEdit
