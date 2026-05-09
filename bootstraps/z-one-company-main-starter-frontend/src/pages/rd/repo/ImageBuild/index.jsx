import { useState } from 'react'
import { ProTable } from '@ant-design/pro-components'
import { Button, Space, Tag, message, Drawer, Descriptions, Modal, Form, Input, Select } from 'antd'
import { EyeOutlined } from '@ant-design/icons'
import { opsApi } from '@/services/api'

const ImageBuild = () => {
  const [detailVisible, setDetailVisible] = useState(false)
  const [detailRecord, setDetailRecord] = useState(null)
  const [modalVisible, setModalVisible] = useState(false)
  const [form] = Form.useForm()

  const statusColor = (s) => {
    if (s === 'success') return 'success'
    if (s === 'failed') return 'error'
    return 'warning'
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 80, ellipsis: true },
    { title: '镜像名', dataIndex: 'imageName', ellipsis: true },
    { title: '版本', dataIndex: 'tag', width: 80 },
    { title: '应用', dataIndex: 'appName', ellipsis: true },
    { title: '分支', dataIndex: 'branch', width: 80 },
    { title: '环境', dataIndex: 'env', width: 60 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (_, r) => <Tag color={statusColor(r.status)}>{r.status}</Tag>,
    },
    { title: '镜像标签', dataIndex: 'imageTag', ellipsis: true },
    { title: '创建时间', dataIndex: 'createdAt', valueType: 'dateTime', width: 160 },
    {
      title: '操作',
      valueType: 'option',
      width: 60,
      render: (_, record) => (
        <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => { setDetailRecord(record); setDetailVisible(true) }}>
          详情
        </Button>
      ),
    },
  ]

  const handleAddBuild = () => {
    form.resetFields()
    setModalVisible(true)
  }

  const handleSubmitBuild = async () => {
    try {
      const values = await form.validateFields()
      await opsApi.addImageBuild(values)
      message.success('构建记录已创建')
      setModalVisible(false)
    } catch (e) {
      message.error('操作失败: ' + (e.message || e))
    }
  }

  return (
    <div>
      <ProTable
        headerTitle="构建记录"
        rowKey="id"
        toolBarRender={() => [
          <Button key="add" type="primary" onClick={handleAddBuild}>
            + 构建镜像
          </Button>,
        ]}
        request={async (params) => {
          try {
            const res = await opsApi.pageImageBuild({
              current: params.current || 1,
              size: params.pageSize || 10,
              imageName: params.imageName,
              appName: params.appName,
              branch: params.branch,
              env: params.env,
              status: params.status,
            })
            return { data: res.records, total: res.total, success: true }
          } catch (e) {
            return { data: [], total: 0, success: false }
          }
        }}
        columns={columns}
        pagination={{ pageSize: 10 }}
        search={{ labelWidth: 'auto' }}
        params={{}}
        form={{
          syncToUrl: (values) => values,
        }}
      />

      <Drawer
        title="构建详情"
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
        width={500}
      >
        {detailRecord && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="ID">{detailRecord.id}</Descriptions.Item>
            <Descriptions.Item label="镜像名">{detailRecord.imageName}</Descriptions.Item>
            <Descriptions.Item label="版本">{detailRecord.tag}</Descriptions.Item>
            <Descriptions.Item label="应用">{detailRecord.appName}</Descriptions.Item>
            <Descriptions.Item label="分支">{detailRecord.branch}</Descriptions.Item>
            <Descriptions.Item label="环境">{detailRecord.env}</Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={statusColor(detailRecord.status)}>{detailRecord.status}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="镜像标签">{detailRecord.imageTag}</Descriptions.Item>
            <Descriptions.Item label="构建日志">
              <pre style={{ maxHeight: 300, overflow: 'auto', fontSize: 12, background: '#f5f5f5', padding: 8 }}>
                {detailRecord.buildLog || '无日志'}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">{detailRecord.createdAt}</Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>

      <Modal
        title="构建镜像"
        open={modalVisible}
        onOk={handleSubmitBuild}
        onCancel={() => setModalVisible(false)}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="imageName" label="镜像名" rules={[{ required: true, message: '请输入镜像名' }]}>
            <Input placeholder="例如: myapp" />
          </Form.Item>
          <Form.Item name="tag" label="版本标签" rules={[{ required: true, message: '请输入版本标签' }]}>
            <Input placeholder="例如: latest, v1.0" />
          </Form.Item>
          <Form.Item name="appName" label="应用名">
            <Input placeholder="关联的应用名（可选）" />
          </Form.Item>
          <Form.Item name="branch" label="分支">
            <Input placeholder="分支名（可选）" />
          </Form.Item>
          <Form.Item name="env" label="环境">
            <Select placeholder="选择环境">
              <Select.Option value="dev">开发环境</Select.Option>
              <Select.Option value="test">测试环境</Select.Option>
              <Select.Option value="prod">生产环境</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue="success">
            <Select>
              <Select.Option value="success">成功</Select.Option>
              <Select.Option value="building">构建中</Select.Option>
              <Select.Option value="failed">失败</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="imageTag" label="完整镜像标签">
            <Input placeholder="例如: myapp:latest" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ImageBuild
