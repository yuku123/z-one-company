import { useState } from 'react'
import { ProTable } from '@ant-design/pro-components'
import { Button, Space, message, Drawer, Form, Input, Popconfirm } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { opsApi } from '@/services/api'

const ImageRepo = () => {
  const [drawerVisible, setDrawerVisible] = useState(false)
  const [editRecord, setEditRecord] = useState(null)
  const [form] = Form.useForm()

  const columns = [
    { title: '镜像名', dataIndex: 'name', sorter: true },
    { title: '仓库地址', dataIndex: 'registry', render: (_, r) => r.registry || '本地' },
    {
      title: '标签数',
      dataIndex: 'tagCount',
      render: (_, r) => r.tags?.length || 0,
      sorter: true,
    },
    { title: '创建时间', dataIndex: 'createdAt', valueType: 'dateTime', sorter: true },
    {
      title: '操作',
      valueType: 'option',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确定删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  const handleEdit = (record) => {
    setEditRecord(record)
    form.setFieldsValue({ name: record.name, registry: record.registry })
    setDrawerVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await opsApi.deleteImage({ id })
      message.success('删除成功')
    } catch (e) {
      message.error('删除失败: ' + e.message)
    }
  }

  const handleAdd = () => {
    setEditRecord(null)
    form.resetFields()
    setDrawerVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editRecord) {
        await opsApi.updateImage({ ...values, id: editRecord.id })
        message.success('更新成功')
      } else {
        await opsApi.addImage(values)
        message.success('添加成功')
      }
      setDrawerVisible(false)
    } catch (e) {
      message.error('操作失败: ' + (e.message || e))
    }
  }

  return (
    <div>
      <ProTable
        headerTitle="镜像列表"
        rowKey="id"
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            添加镜像
          </Button>,
        ]}
        request={async (params) => {
          const res = await opsApi.pageImage({
            current: params.current || 1,
            size: params.pageSize || 10,
            name: params.name,
          })
          return { data: res.records, total: res.total, success: true }
        }}
        columns={columns}
        pagination={{ pageSize: 10 }}
      />

      <Drawer
        title={editRecord ? '编辑镜像' : '添加镜像'}
        open={drawerVisible}
        onClose={() => setDrawerVisible(false)}
        onAfterOpenChange={(open) => !open && form.resetFields()}
        footer={
          <div style={{ textAlign: 'right' }}>
            <Button onClick={() => setDrawerVisible(false)} style={{ marginRight: 8 }}>
              取消
            </Button>
            <Button type="primary" onClick={handleSubmit}>
              确定
            </Button>
          </div>
        }
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="镜像名" rules={[{ required: true, message: '请输入镜像名' }]}>
            <Input placeholder="例如: myapp" disabled={!!editRecord} />
          </Form.Item>
          <Form.Item name="registry" label="仓库地址（可选）">
            <Input placeholder="例如: registry.example.com，不填则为本地" />
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}

export default ImageRepo
