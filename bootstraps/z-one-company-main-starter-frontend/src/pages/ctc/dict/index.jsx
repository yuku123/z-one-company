import { useState, useEffect, useMemo } from 'react'
import { Card, Tree, Table, Button, Space, Tag, Modal, Form, Input, Select, message, Popconfirm, Empty, InputNumber, Descriptions } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, ThunderboltOutlined, FieldBinaryOutlined } from '@ant-design/icons'
import { dictApi, getTenantList } from '@/services/api'

const DictPage = () => {
  const [loading, setLoading] = useState(false)
  const [tenantOptions, setTenantOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(null)
  const [allItems, setAllItems] = useState([])
  const [categories, setCategories] = useState([])
  const [selectedCategory, setSelectedCategory] = useState(null)
  const [initStatus, setInitStatus] = useState(null)
  const [itemModalOpen, setItemModalOpen] = useState(false)
  const [editingItem, setEditingItem] = useState(null)
  const [catModalOpen, setCatModalOpen] = useState(false)
  const [schemaModalOpen, setSchemaModalOpen] = useState(false)
  const [schemaFields, setSchemaFields] = useState([])
  const [form] = Form.useForm()
  const [catForm] = Form.useForm()
  const [schemaForm] = Form.useForm()

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
        dictApi.list(selectedTenant).catch(() => []),
        dictApi.categories(selectedTenant).catch(() => []),
        dictApi.hasInit(selectedTenant).catch(() => false),
      ])
      setAllItems(items || [])
      setCategories(cats || [])
      setInitStatus(hasInit)
    } catch (e) { message.error('加载失败') } finally { setLoading(false) }
  }

  const handleInit = async () => {
    try { await dictApi.init(selectedTenant, ''); message.success('初始化成功'); loadData() }
    catch (e) { message.error('初始化失败') }
  }

  // 当前分类下的条目 + schema
  const currentItems = useMemo(() => allItems.filter(i => i.category === selectedCategory), [allItems, selectedCategory])
  const catSchema = useMemo(() => {
    if (!currentItems.length || !currentItems[0].extSchema) return []
    try { return JSON.parse(currentItems[0].extSchema) } catch { return [] }
  }, [currentItems])

  const handleDeleteItem = async (item) => {
    try { await dictApi.delete(item.id); message.success('删除成功'); loadData() }
    catch (e) { message.error('删除失败') }
  }

  // ===== 分类树 =====
  const treeTitleRender = (nodeData) => {
    const cat = nodeData.key
    const cnt = allItems.filter(i => i.category === cat).length
    const isBuiltin = allItems.filter(i => i.category === cat).some(i => i.isBuiltin)
    return (
      <span style={{ display: 'flex', alignItems: 'center', gap: 4, width: '100%' }}>
        {isBuiltin ? <Tag color="blue" style={{ fontSize: 10, marginRight: 2 }}>内置</Tag>
                  : <Tag color="orange" style={{ fontSize: 10, marginRight: 2 }}>自定义</Tag>}
        <span style={{ flex: 1 }}>{cat} ({cnt})</span>
        <Button type="text" size="small" icon={<PlusOutlined style={{ fontSize: 10 }} />}
          onClick={(e) => { e.stopPropagation(); setEditingItem(null); form.resetFields(); form.setFieldsValue({ category: cat }); setItemModalOpen(true) }} />
        <Popconfirm title={`删除分类 "${cat}"？`} description="将删除该分类下所有条目"
          onConfirm={() => { allItems.filter(i => i.category === cat).forEach(i => dictApi.delete(i.id)); loadData(); if (selectedCategory === cat) setSelectedCategory(null) }}
          okText="确定" cancelText="取消">
          <Button type="text" size="small" danger icon={<DeleteOutlined style={{ fontSize: 10 }} />}
            onClick={(e) => e.stopPropagation()} />
        </Popconfirm>
      </span>
    )
  }

  // ===== Schema =====
  const handleSchemaEdit = () => {
    const fields = catSchema.length > 0 ? catSchema : [{ field: '', label: '', type: 'text' }]
    setSchemaFields(fields)
    const fv = {}
    fields.forEach((f, i) => { fv[`field_${i}`] = f.field; fv[`label_${i}`] = f.label; fv[`type_${i}`] = f.type })
    schemaForm.setFieldsValue(fv)
    setSchemaModalOpen(true)
  }

  const handleSchemaSave = async () => {
    const values = schemaForm.getFieldsValue()
    const fields = []
    let i = 0
    while (values[`field_${i}`] !== undefined) {
      if (values[`field_${i}`]) {
        fields.push({ field: values[`field_${i}`], label: values[`label_${i}`] || '', type: values[`type_${i}`] || 'text' })
      }
      i++
    }
    const extSchema = JSON.stringify(fields)
    try {
      if (currentItems.length > 0) {
        await dictApi.update({ id: currentItems[0].id, extSchema })
      } else {
        await dictApi.add({ category: selectedCategory, dictKey: '__schema__', dictValue: '', extSchema, tenantCode: selectedTenant })
      }
      message.success('Schema 保存成功')
      setSchemaModalOpen(false)
      loadData()
    } catch (e) { message.error('Schema 保存失败: ' + (e.message || e)) }
  }

  // 动态列（基础列 + schema 扩展列）
  const columns = [
    { title: 'Key', dataIndex: 'dictKey', key: 'dictKey', width: 120 },
    { title: 'Value', dataIndex: 'dictValue', key: 'dictValue' },
    ...catSchema.map(sf => ({
      title: sf.label || sf.field,
      dataIndex: ['extDataParsed', sf.field],
      key: sf.field,
      render: (_, r) => {
        try { return r.extData ? (JSON.parse(r.extData)[sf.field] || '-') : '-' }
        catch { return '-' }
      },
    })),
    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 60 },
    { title: '类型', dataIndex: 'isBuiltin', key: 'isBuiltin', width: 70,
      render: (v) => v ? <Tag color="blue" style={{ fontSize: 10 }}>内置</Tag> : <Tag color="orange" style={{ fontSize: 10 }}>自定义</Tag> },
    { title: '操作', key: 'action', width: 120,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => {
              setEditingItem(record)
              const fv = { ...record }
              if (record.extData) {
                try { Object.assign(fv, JSON.parse(record.extData)) } catch {}
              }
              form.setFieldsValue(fv)
              setItemModalOpen(true)
            }}>编辑</Button>
          {!record.isBuiltin && (
            <Popconfirm title="确认删除？" onConfirm={() => handleDeleteItem(record)}>
              <Button type="link" danger size="small" icon={<DeleteOutlined />}>删除</Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      {/* 左侧分类树 */}
      <Card size="small" title="元典管理" style={{ width: 320, flexShrink: 0, overflow: 'auto' }}
        extra={
          <Space size={4}>
            {selectedTenant && initStatus === false && <Button size="small" icon={<ThunderboltOutlined />} onClick={handleInit}>初始化</Button>}
            <Button size="small" icon={<PlusOutlined />} onClick={() => { setCatModalOpen(true); catForm.resetFields() }} />
            <Button size="small" icon={<ReloadOutlined />} onClick={loadData} />
          </Space>
        }>
        <Select placeholder="选择租户" style={{ width: '100%', marginBottom: 8 }}
          value={selectedTenant} onChange={setSelectedTenant} allowClear options={tenantOptions} />
        {selectedTenant ? (
          categories.length > 0
            ? <Tree treeData={categories.map(c => ({ key: c, title: null }))} showLine showIcon={false}
                selectedKeys={selectedCategory ? [selectedCategory] : []}
                onSelect={(keys) => setSelectedCategory(keys[0] || null)}
                titleRender={treeTitleRender} />
            : <Empty description={initStatus === false ? '未初始化' : '暂无数据'} />
        ) : <Empty description="请选择租户" />}
      </Card>

      {/* 右侧 */}
      <Card size="small" style={{ flex: 1, overflow: 'auto' }}
        title={selectedCategory ? `${selectedCategory} - 枚举条目` : '枚举条目'}
        extra={selectedCategory ? (
          <Space>
            <Button size="small" icon={<FieldBinaryOutlined />} onClick={handleSchemaEdit}>字段描述</Button>
            <Button type="primary" size="small" icon={<PlusOutlined />}
              onClick={() => { setEditingItem(null); form.resetFields(); form.setFieldsValue({ category: selectedCategory }); setItemModalOpen(true) }}>新增</Button>
          </Space>
        ) : null}>
        {selectedCategory ? (
          <Table columns={columns} dataSource={currentItems.map((r, i) => ({ ...r, key: r.id || i }))}
            pagination={false} size="small" locale={{ emptyText: '暂无条目' }} />
        ) : <Empty description="请从左侧选择枚举分类" />}
      </Card>

      {/* 新增分类弹窗（含 Schema 定义） */}
      <Modal title="新增枚举分类" open={catModalOpen} width={650}
        onCancel={() => setCatModalOpen(false)}
        onOk={() => catForm.submit()} destroyOnClose>
        <Form form={catForm} layout="vertical" onFinish={async (v) => {
          try {
            // 收集 schema 字段
            const fields = []; let i = 0
            while (v[`field_${i}`] !== undefined) {
              if (v[`field_${i}`]) fields.push({ field: v[`field_${i}`], label: v[`label_${i}`] || '', type: v[`type_${i}`] || 'text' })
              i++
            }
            const extSchema = fields.length > 0 ? JSON.stringify(fields) : undefined
            await dictApi.add({ category: v.category, dictKey: v.category, dictValue: v.label || v.category, extSchema, tenantCode: selectedTenant })
            message.success('创建成功'); setCatModalOpen(false); loadData()
          } catch (e) { message.error('创建失败') }
        }}>
          <Form.Item name="category" label="分类名称" rules={[{ required: true }]}><Input placeholder="如：server_env" /></Form.Item>
          <Form.Item name="label" label="显示标签"><Input placeholder="如：服务器环境" /></Form.Item>
          <div style={{ fontWeight: 500, marginBottom: 8, color: '#1677ff' }}>字段定义（可选，定义后条目自动按此结构展示）</div>
          <Table dataSource={(catForm.getFieldValue ? (catForm.getFieldValue('_schemaCount') || 0) : 0) > 0
            ? Array.from({ length: catForm.getFieldValue('_schemaCount') || 1 }, (_, i) => ({ key: i, idx: i }))
            : [{ key: 0, idx: 0 }]} pagination={false} size="small"
            columns={[
              { title: '字段名', dataIndex: 'field', key: 'field',
                render: (_, r) => <Form.Item name={`field_${r.idx}`} noStyle>
                  <Input size="small" placeholder="如：env" /></Form.Item> },
              { title: '显示名称', dataIndex: 'label', key: 'label',
                render: (_, r) => <Form.Item name={`label_${r.idx}`} noStyle>
                  <Input size="small" placeholder="如：环境" /></Form.Item> },
              { title: '类型', dataIndex: 'type', key: 'type', width: 100,
                render: (_, r) => <Form.Item name={`type_${r.idx}`} initialValue="text" noStyle>
                  <Select size="small"><Select.Option value="text">文本</Select.Option><Select.Option value="number">数字</Select.Option></Select>
                </Form.Item> },
            ]}
            footer={() => (
              <Button type="dashed" size="small" block icon={<PlusOutlined />}
                onClick={() => {
                  const c = catForm.getFieldValue('_schemaCount') || 0
                  // force re-render by adding to schemaFields
                  setCatSchemaCount(c + 1)
                }}>新增字段</Button>
            )} />
        </Form>
      </Modal>

      {/* 字段描述弹窗 */}
      <Modal title="字段描述定义" open={schemaModalOpen} width={600}
        onCancel={() => setSchemaModalOpen(false)}
        onOk={handleSchemaSave} destroyOnClose>
        <Form form={schemaForm} layout="vertical">
          <Table dataSource={schemaFields.map((f, i) => ({ ...f, key: i, idx: i }))} pagination={false} size="small"
            columns={[
              { title: '字段名', dataIndex: 'field', key: 'field',
                render: (_, r) => <Form.Item name={`field_${r.idx}`} initialValue={r.field} noStyle>
                  <Input size="small" placeholder="如：env" /></Form.Item> },
              { title: '显示名称', dataIndex: 'label', key: 'label',
                render: (_, r) => <Form.Item name={`label_${r.idx}`} initialValue={r.label} noStyle>
                  <Input size="small" placeholder="如：环境" /></Form.Item> },
              { title: '类型', dataIndex: 'type', key: 'type', width: 100,
                render: (_, r) => <Form.Item name={`type_${r.idx}`} initialValue={r.type || 'text'} noStyle>
                  <Select size="small">
                    <Select.Option value="text">文本</Select.Option>
                    <Select.Option value="number">数字</Select.Option>
                    <Select.Option value="select">下拉</Select.Option>
                  </Select></Form.Item> },
            ]}
            footer={() => (
              <Button type="dashed" size="small" block icon={<PlusOutlined />}
                onClick={() => setSchemaFields([...schemaFields, { field: '', label: '', type: 'text' }])}>新增字段</Button>
            )} />
        </Form>
      </Modal>

      {/* 条目编辑弹窗 */}
      <Modal title={editingItem ? '编辑条目' : '新增条目'} open={itemModalOpen} width={600}
        onCancel={() => { setItemModalOpen(false); setEditingItem(null) }}
        onOk={() => form.submit()} destroyOnClose>
        <Form form={form} layout="vertical" onFinish={async (values) => {
          const extData = {}
          catSchema.forEach(sf => { if (values[sf.field] !== undefined) extData[sf.field] = values[sf.field] })
          const payload = { ...values, extData: Object.keys(extData).length > 0 ? JSON.stringify(extData) : undefined }
          try {
            if (editingItem) { await dictApi.update({ ...payload, id: editingItem.id }); message.success('更新成功') }
            else { await dictApi.add({ ...payload, tenantCode: selectedTenant }); message.success('创建成功') }
            setItemModalOpen(false); setEditingItem(null); loadData()
          } catch (e) { message.error('操作失败') }
        }}>
          <Form.Item name="category" label="分类" rules={[{ required: true }]}>
            <Select disabled={!!editingItem} mode="tags" maxCount={1}
              options={categories.map(c => ({ label: c, value: c }))} />
          </Form.Item>
          <Form.Item name="dictKey" label="Key" rules={[{ required: true }]}>
            <Input disabled={!!editingItem} /></Form.Item>
          <Form.Item name="dictValue" label="Value" rules={[{ required: true }]}><Input /></Form.Item>
          {/* schema 扩展字段 */}
          {catSchema.map(sf => (
            <Form.Item key={sf.field} name={sf.field} label={sf.label || sf.field}>
              {sf.type === 'number' ? <InputNumber style={{ width: '100%' }} /> :
               sf.type === 'select' ? <Select mode="tags" maxCount={1}
                 options={sf.options?.map(o => ({ label: o, value: o })) || []} /> :
               <Input />}
            </Form.Item>
          ))}
          <Form.Item name="sortOrder" label="排序" initialValue={0}><Input /></Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default DictPage
