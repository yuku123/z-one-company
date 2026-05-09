import { useState, useEffect } from 'react'
import { ProTable } from '@ant-design/pro-components'
import { Button, Space, message, Select, Modal, Form, Input, Popconfirm } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { opsApi } from '@/services/api'

const ImageTag = () => {
  const [images, setImages] = useState([])
  const [selectedImage, setSelectedImage] = useState(null)
  const [tags, setTags] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [form] = Form.useForm()

  useEffect(() => {
    loadImages()
  }, [])

  const loadImages = async () => {
    try {
      const res = await opsApi.listImage({})
      setImages(res || [])
    } catch (e) {
      message.error('加载镜像列表失败')
    }
  }

  const loadTags = async (imageId) => {
    try {
      const res = await opsApi.listImageTags({ imageId })
      setTags(res || [])
    } catch (e) {
      setTags([])
    }
  }

  const handleImageChange = (value) => {
    setSelectedImage(value)
    loadTags(value)
  }

  const handleAddTag = () => {
    if (!selectedImage) {
      message.warning('请先选择镜像')
      return
    }
    form.resetFields()
    setModalVisible(true)
  }

  const handleSubmitTag = async () => {
    try {
      const values = await form.validateFields()
      await opsApi.addImageTag({ imageId: selectedImage, tag: values.tag })
      message.success('添加成功')
      setModalVisible(false)
      loadTags(selectedImage)
    } catch (e) {
      message.error('添加失败: ' + (e.message || e))
    }
  }

  const handleDeleteTag = async (tagId) => {
    try {
      await opsApi.deleteImageTag({ id: tagId })
      message.success('删除成功')
      loadTags(selectedImage)
    } catch (e) {
      message.error('删除失败: ' + e.message)
    }
  }

  const columns = [
    { title: '标签', dataIndex: 'tag' },
    { title: '创建时间', dataIndex: 'createdAt', valueType: 'dateTime' },
    {
      title: '操作',
      valueType: 'option',
      render: (_, record) => (
        <Popconfirm title="确定删除该版本？" onConfirm={() => handleDeleteTag(record.id)}>
          <Button type="link" size="small" danger>
            删除
          </Button>
        </Popconfirm>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', gap: 8, alignItems: 'center' }}>
        <span>选择镜像：</span>
        <Select
          placeholder="请选择镜像"
          style={{ width: 240 }}
          value={selectedImage}
          onChange={handleImageChange}
          allowClear
        >
          {images.map((img) => (
            <Select.Option key={img.id} value={img.id}>
              {img.name}
            </Select.Option>
          ))}
        </Select>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAddTag} disabled={!selectedImage}>
          添加版本
        </Button>
      </div>

      <ProTable
        headerTitle={selectedImage ? `标签列表` : '请先选择镜像'}
        rowKey="id"
        dataSource={tags}
        columns={columns}
        pagination={false}
        search={false}
        toolBarRender={false}
        options={false}
      />

      <Modal
        title="添加镜像版本"
        open={modalVisible}
        onOk={handleSubmitTag}
        onCancel={() => setModalVisible(false)}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="tag" label="版本标签" rules={[{ required: true, message: '请输入版本标签' }]}>
            <Input placeholder="例如: latest, v1.0" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ImageTag
