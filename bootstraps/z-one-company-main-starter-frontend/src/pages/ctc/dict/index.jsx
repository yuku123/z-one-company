import { useState, useEffect } from 'react'
import {
  Card, Tree, Button, Space, Tag, Table, Modal, Form, Input, Select, message, Popconfirm, Empty, Descriptions, Spin
} from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, ThunderboltOutlined } from '@ant-design/icons'
import { dictApi } from '@/services/api'
import { getTenantList } from '@/services/api'

const DictPage = () => {
  const [loading, setLoading] = useState(false)
  const [tenantOptions, setTenantOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(null)
  const [allItems, setAllItems] = useState([])
  const [categories, setCategories] = useState([])
  const [selectedCategory, setSelectedCategory] = useState(null)
  const [selectedItem, setSelectedItem] = useState(null)
  const [initStatus, setInitStatus] = useState(null) // null/true/false
  const [modalOpen, setModalOpen] = useState(false)
  const [editingItem, setEditingItem] = useState(null)
  const [form] = Form.useForm()

  useEffect(() => {
    (async () => {
      const tenants = await getTenantList()
      setTenantOptions((tenants || []).map(t => ({ label: `${t.tenantName} (${t.tenantCode})`, value: t.tenantCode })))
    })()
  }, [])

  useEffect(() => {
    if (!selectedTenant) { setAllItems([]); setCategories([]); setSelectedCategory(null); setInitStatus(null); return }
    loadData()
  }, [selectedTenant])

  const loadData = async () => {
    setLoading(true)
    try {
      const [items, cats, hasInit] = await Promise.all([
        dictApi.list(selectedTenant),
        dictApi.categories(selectedTenant),
        dictApi.hasInit(selectedTenant),
      ])
      setAllItems(items || [])
      setCategories(cats || [])
      setInitStatus(hasInit)
    } catch (e) { message.error('加载失败') } finally { setLoading(false) }
  }

  const handleInit = async () => {
    try {
      await dictApi.init(selectedTenant, '')
      message.success('初始化成功')
      loadData()
    } catch (e) { message.error('初始化失败') }
  }

  // 当前分类下的条目
  const catItems = allItems.filter(i => i.category === selectedCategory)
  const treeData = categories.map(cat => ({
    key: cat,
    title: cat,
    type: 'category',
    children: allItems.filter(i => i.category === cat).map(item => ({
      key: `item:${item.id}`,
      title: <span>{item.dictKey} - {item.dictValue} {item.isBuiltin ? <Tag color="blue" style={{ fontSize: 10, marginLeft: 4 }}>内置</Tag> : <Tag color="orange" style={{ fontSize: 10, marginLeft: 4 }}>自定义</Tag>}</span>,
      type: 'item',
      data: item,
      isLeaf: true,
    })),
  }))

  const treeTitleRender = (nodeData) => {
    if (nodeData.type === 'category') {
      return <span><Tag color="purple">分类</Tag>{nodeData.key}</span>
    }
    const item = nodeData.data
    return (
      <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
        <span>{item.dictKey} - {item.dictValue}</span>
        {item.isBuiltin ? <Tag color="blue" style={{ fontSize: 10 }}>内置</Tag> : <Tag color="orange" style={{ fontSize: 10 }}>自定义</Tag>}
        {!item.isBuiltin && (
          <Popconfirm title="确认删除？" onConfirm={(e) => { e?.stopPropagation(); handleDelete(item) }}
            onCancel={(e) => e?.stopPropagation()}>
            <Button type="text" size="small" danger icon={<DeleteOutlined style={{ fontSize: 11 }} />}
              onClick={(e) => e.stopPropagation()} />
          </Popconfirm>
        )}
      </span>
    )
  }

  const handleDelete = async (item) => {
    try { await dictApi.delete(item.id); message.success('删除成功'); loadData() }
    catch (e) { message.error('删除失败') }
  }

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      <Card size="small" title="元典管理" style={{ width: 360, flexShrink: 0, overflow: 'auto' }}
        extra={
          <Space size={4}>
            {selectedTenant && initStatus === false && (
              <Button size="small" icon={<ThunderboltOutlined />} onClick={handleInit}>初始化</Button>
            )}
            {selectedTenant && <Button size="small" icon={<ReloadOutlined />} onClick={loadData} />}
          </Space>
        }>
        <Select placeholder="选择租户" style={{ width: '100%', marginBottom: 8 }}
          value={selectedTenant} onChange={setSelectedTenant} allowClear options={tenantOptions} />
        <Spin spinning={loading}>
          {selectedTenant ? (
            treeData.length > 0
              ? <Tree treeData={treeData} showLine showIcon={false} defaultExpandAll
                  onSelect={(keys, info) => {
                    if (info.node.type === 'item') setSelectedItem(info.node.data)
                  }} titleRender={treeTitleRender} />
              : <Empty description={initStatus === false ? '未初始化，请点击初始化按钮' : '暂无数据'} />
          ) : (
            <Empty description="请选择租户" />
          )}
        </Spin>
      </Card>

      {/* 右侧 */}
      <Card size="small" style={{ flex: 1, overflow: 'auto' }}>
        {!selectedItem ? (
          <Empty description="请选择条目查看详情" />
        ) : (
          <>
            <Descriptions title="条目详情" size="small" bordered column={2} style={{ marginBottom: 16 }}>
              <Descriptions.Item label="分类">{selectedItem.category}</Descriptions.Item>
              <Descriptions.Item label="Key">{selectedItem.dictKey}</Descriptions.Item>
              <Descriptions.Item label="Value">{selectedItem.dictValue}</Descriptions.Item>
              <Descriptions.Item label="排序">{selectedItem.sortOrder}</Descriptions.Item>
              <Descriptions.Item label="类型">
                {selectedItem.isBuiltin ? <Tag color="blue">内置</Tag> : <Tag color="orange">自定义</Tag>}
              </Descriptions.Item>
              <Descriptions.Item label="描述">{selectedItem.description || '-'}</Descriptions.Item>
            </Descriptions>
            {!selectedItem.isBuiltin && (
              <Button type="primary" size="small" icon={<EditOutlined />}
                onClick={() => { setEditingItem(selectedItem); form.setFieldsValue(selectedItem); setModalOpen(true) }}>
                编辑
              </Button>
            )}
          </>
        )}
      </Card>

      <Modal title="新增/编辑条目" open={modalOpen}
        onCancel={() => { setModalOpen(false); setEditingItem(null) }}
        onOk={() => form.submit()} destroyOnClose>
        <Form form={form} layout="vertical" onFinish={async (values) => {
          try {
            if (editingItem) { await dictApi.update({ ...values, id: editingItem.id }); message.success('更新成功') }
            else { await dictApi.add({ ...values, tenantCode: selectedTenant }); message.success('创建成功') }
            setModalOpen(false); setEditingItem(null); loadData()
          } catch (e) { message.error('操作失败') }
        }}>
          <Form.Item name="category" label="分类" rules={[{ required: true }]}>
            <Input disabled={!!editingItem} placeholder="如：gender" />
          </Form.Item>
          <Form.Item name="dictKey" label="Key" rules={[{ required: true }]}>
            <Input disabled={!!editingItem} placeholder="如：M" />
          </Form.Item>
          <Form.Item name="dictValue" label="Value" rules={[{ required: true }]}>
            <Input placeholder="如：男" />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序" initialValue={0}>
            <Input placeholder="数字越小越靠前" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default DictPage
