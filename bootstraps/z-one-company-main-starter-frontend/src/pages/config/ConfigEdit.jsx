import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams, useLocation } from 'react-router-dom'
import { Card, Form, Input, Button, message, Space, Select } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { configApi } from '@/services/api'

const ConfigEdit = () => {
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [isEdit, setIsEdit] = useState(false)
  const [searchParams] = useSearchParams()
  const location = useLocation()
  const [namespaceList, setNamespaceList] = useState([])

  useEffect(() => {
    (async () => {
      try {
        const records = await configApi.clusterList()
        setNamespaceList((records || []).map(i => ({ label: i.name, value: i.name })))
      } catch (e) { console.error(e) }
    })()
  }, [])

  useEffect(() => {
    const state = location.state
    if (state && state.config) {
      setIsEdit(true)
      form.setFieldsValue({
        dataId: state.config.dataId, group: state.config.group,
        content: state.config.content, appName: state.config.appName,
        namespace: state.config.namespace, configDesc: state.config.configDesc,
      })
    } else {
      const dataId = searchParams.get('dataId'), group = searchParams.get('group')
      const namespace = searchParams.get('namespace')
      if (dataId) form.setFieldsValue({ dataId })
      if (group) form.setFieldsValue({ group })
      form.setFieldsValue({ namespace: namespace || 'DEFAULT_NAMESPACE' })
    }
  }, [])

  const onFinish = async (values) => {
    setLoading(true)
    try {
      await configApi.saveConfig(values)
      message.success(isEdit ? '修改成功' : '保存成功')
      navigate('/config/list')
    } catch (error) {
      message.error(isEdit ? '修改失败' : '保存失败')
    } finally { setLoading(false) }
  }

  return (
    <Card title={<Space><Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/config/list')}>返回</Button>
      <span>{isEdit ? '编辑配置' : '新建配置'}</span></Space>}>
      <Form form={form} layout="vertical" onFinish={onFinish} style={{ maxWidth: 800 }}>
        <Form.Item name="namespace" label="命名空间" rules={[{ required: true }]}>
          <Select placeholder="请选择命名空间" options={namespaceList} />
        </Form.Item>
        <Form.Item name="appName" label="应用名"><Input placeholder="请输入应用名（可选）" /></Form.Item>
        <Form.Item name="dataId" label="Data ID" rules={[{ required: true }]}>
          <Input disabled={isEdit} placeholder="如：application.yml" /></Form.Item>
        <Form.Item name="group" label="Group" rules={[{ required: true }]} initialValue="DEFAULT_GROUP">
          <Input disabled={isEdit} placeholder="请输入 Group" /></Form.Item>
        <Form.Item name="content" label="配置内容" rules={[{ required: true }]}>
          <Input.TextArea rows={15} style={{ fontFamily: 'monospace' }} /></Form.Item>
        <Form.Item name="configDesc" label="描述"><Input.TextArea rows={3} /></Form.Item>
        <Form.Item><Space>
          <Button type="primary" htmlType="submit" loading={loading}>{isEdit ? '保存修改' : '保存'}</Button>
          <Button onClick={() => navigate('/config/list')}>取消</Button>
        </Space></Form.Item>
      </Form>
    </Card>
  )
}

export default ConfigEdit
