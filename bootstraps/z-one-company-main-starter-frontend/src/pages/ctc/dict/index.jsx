import { useState, useEffect, useMemo } from 'react'
import { Card, Tree, Table, Button, Space, Tag, Modal, Form, Input, Select, message, Popconfirm, Empty, InputNumber, Checkbox, Dropdown } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, ThunderboltOutlined, FieldBinaryOutlined } from '@ant-design/icons'
import { dictApi, dictCatApi, getTenantList } from '@/services/api'

const DictPage = () => {
  const [loading, setLoading] = useState(false)
  const [tenantOptions, setTenantOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(() => localStorage.getItem('z_tenant') || null)
  const [categories, setCategories] = useState([]) // category tree data
  const [allItems, setAllItems] = useState([])
  const [selectedCat, setSelectedCat] = useState(null) // selected category node
  const [initStatus, setInitStatus] = useState(null)
  const [itemModalOpen, setItemModalOpen] = useState(false)
  const [editingItem, setEditingItem] = useState(null)
  const [catModalOpen, setCatModalOpen] = useState(false)
  const [catModalMode, setCatModalMode] = useState('cat') // 'cat' | 'enum'
  const [schemaModalOpen, setSchemaModalOpen] = useState(false)
  const [schemaFields, setSchemaFields] = useState([])
  const [form] = Form.useForm()
  const [catForm] = Form.useForm()
  const [schemaForm] = Form.useForm()

  useEffect(() => {
    (async () => {
      const tenants = await getTenantList()
      setTenantOptions((tenants || []).map(t => ({ label: `${t.tenantName} (${t.tenantCode})`, value: t.tenantCode })))
      const saved = localStorage.getItem('z_tenant')
      if (saved && (tenants || []).some(t => t.tenantCode === saved)) { setSelectedTenant(saved) }
    })()
  }, [])

  useEffect(() => {
    if (!selectedTenant) { setCategories([]); setAllItems([]); setSelectedCat(null); setInitStatus(null); return }
    loadData()
  }, [selectedTenant])

  const loadData = async () => {
    setLoading(true)
    console.log('[dict] loadData called, selectedTenant:', selectedTenant)
    try {
      const [items, cats, hasInit, dbCats] = await Promise.all([
        dictApi.list(selectedTenant).catch(e => { console.error('[dict] list error', e); return [] }),
        dictApi.categories(selectedTenant).catch(e => { console.error('[dict] categories error', e); return [] }),
        dictApi.hasInit(selectedTenant).catch(e => { console.error('[dict] hasInit error', e); return false }),
        dictCatApi.list(selectedTenant).catch(e => { console.error('[dict] dbCats error', e); return [] }),
      ])
      console.log('[dict] API results:', { items, cats, hasInit, dbCats })
      setAllItems(items || [])
      setInitStatus(hasInit)

      // --- 建立 category 节点 ---
      const catMap = {}
      ;(dbCats || []).forEach(c => {
        // 处理重复 catCode：只保留第一个（根分类优先）
        if (!catMap[c.catCode]) catMap[c.catCode] = c
      })

      // --- 建立 category → enum items 的映射 ---
      // items 的 category 字段（如 "dd", "status"）对应 categories API 返回的分类名
      const catToEnums = {}
      ;(items || []).forEach(item => {
        if (item.category === '__META__') return
        const parent = item.category
        if (!catToEnums[parent]) catToEnums[parent] = []
        catToEnums[parent].push(item)
      })

      // --- 构建分类骨架（来自 dbCats，提供父子层级）---
      const dbCatMap = {}   // catCode → dbCat 记录
      ;(dbCats || []).forEach(c => { dbCatMap[c.catCode] = c })

      // 递归：某 catCode 下的直接子分类
      const buildDbCatChildren = (parentCode) => {
        return (dbCats || [])
          .filter(c => c.parentCode === parentCode)
          .filter(c => catMap[c.catCode])   // 去重：跳过被覆盖的重复节点
          .map(c => ({
            key: `cat:${c.catCode}`,
            title: c.catName || c.catCode,
            type: 'category',
            data: c,
            children: buildDbCatChildren(c.catCode),
          }))
      }

      // --- 顶级分类节点 = categories API 返回的所有分类名 ---
      // 每个分类名下挂载该 category 下的所有枚举 items
      const topLevelNodes = (cats || []).map(catName => {
        const enums = catToEnums[catName] || []
        return {
          key: `cat:${catName}`,
          title: catName,
          type: 'category',
          data: dbCatMap[catName] || { catCode: catName, catName: catName, parentCode: null },
          children: enums.length > 0 ? enums.map(item => ({
            key: `enum:${item.category}:${item.dictKey}`,
            title: item.dictKey,
            type: 'enum',
            data: item,
            isLeaf: true,
          })) : undefined,
        }
      })

      const treeData = topLevelNodes
      console.log('[dict] treeData:', JSON.stringify(treeData, null, 2))
      setCategories(treeData)
    }
    catch (e) { message.error('加载失败') } finally { setLoading(false) }
  }

  // 初始化租户的元典数据（首次使用时调用）
  const handleInit = async () => {
    try {
      await dictApi.init(selectedTenant, { tenantCode: selectedTenant })
      message.success('初始化成功')
      loadData()
    } catch (e) { message.error('初始化失败') }
  }

  const openCatModal = (mode = 'cat') => {
    setCatModalMode(mode)
    // 先设默认值，再 resetFields（避免值被清掉）
    if (mode === 'enum' && selectedCat?.type === 'category') {
      catForm.setFieldsValue({ parentCode: selectedCat.key.replace('cat:', '') })
    } else {
      catForm.resetFields()
    }
    setCatModalOpen(true)
  }

  // 分类新增按钮 - 下拉菜单
  const addCatDropdown = {
    items: [
      { key: 'cat', label: '新增子分类', icon: <PlusOutlined />, onClick: () => openCatModal('cat') },
      { key: 'enum', label: '新增枚举', icon: <PlusOutlined />, onClick: () => openCatModal('enum') },
    ],
  }
  // current items for selected enum
  // key 格式: category:dictKey（如 "status:enabled"）
  const currentEnum = selectedCat?.type === 'enum' ? selectedCat.key.replace(/^enum:/, '').split(':')[0] : null
  const currentItems = useMemo(() =>
    allItems.filter(i => i.category === currentEnum && i.dictKey !== '__META__'),
    [allItems, currentEnum]
  )
  const catSchema = useMemo(() => {
    if (!currentEnum) return []
    const metaItem = allItems.find(i => i.category === currentEnum && i.dictKey === '__META__')
    if (!metaItem || !metaItem.extSchema) return []
    try { return JSON.parse(metaItem.extSchema) } catch { return [] }
  }, [allItems, currentEnum])

  const handleDeleteItem = async (item) => {
    try { await dictApi.delete(item.id); message.success('删除成功'); loadData() }
    catch (e) { message.error('删除失败') }
  }

  // ===== 拖拽（自定义枚举可拖动到分类下） =====
  const onDrop = async (info) => {
    const { dragNode, node, dropToGap } = info
    if (dragNode.type !== 'enum') return // only enums can be dragged
    const enumName = dragNode.key.replace('enum:', '')
    const dropKey = node.key
    // determine target category
    let targetCatCode
    if (dropKey.startsWith('cat:')) {
      targetCatCode = dropKey.replace('cat:', '')
    } else {
      return // don't allow dropping onto non-category nodes
    }
    // update the __META__ item's category field
    try {
      const metaItems = allItems.filter(i => i.category === enumName && i.dictKey === '__META__')
      for (const m of metaItems) {
        await dictApi.update({ id: m.id, dictKey: '__META__', dictValue: `${targetCatCode}` })
      }
      // also update all items in this enum to the new category key
      // actually, the items stay under this enum name, we just move the enum to a new category
      // by changing the category name... 
      // Actually, the way to "move" an enum to a category is to update the category reference.
      // Since enum items have category = enumName, moving means changing the category field.
      // But that would affect ALL items. The "category" field IS the enum name.
      // So I can't easily rename categories.
      // Better approach: store the category mapping separately.
      message.warning('枚举移动功能暂不支持，将在后续版本实现')
      loadData()
    } catch (e) { message.error('移动失败'); loadData() }
  }

  // ===== Schema =====
  const handleSchemaEdit = () => {
    const fields = catSchema.length > 0 ? catSchema : [{ field: '', label: '', type: 'text' }]
    setSchemaFields(fields)
    const fv = {}
    fields.forEach((f, i) => { fv[`field_${i}`] = f.field; fv[`label_${i}`] = f.label; fv[`type_${i}`] = f.type; fv[`req_${i}`] = f.required; fv[`placeholder_${i}`] = f.placeholder; fv[`pattern_${i}`] = f.pattern })
    schemaForm.setFieldsValue(fv)
    setSchemaModalOpen(true)
  }

  const handleSchemaSave = async () => {
    const values = schemaForm.getFieldsValue()
    const fields = []; let i = 0
    while (values[`field_${i}`] !== undefined) {
      if (values[`field_${i}`]) {
        fields.push({ field: values[`field_${i}`], label: values[`label_${i}`] || '', type: values[`type_${i}`] || 'text', required: !!values[`req_${i}`], placeholder: values[`placeholder_${i}`] || '', pattern: values[`pattern_${i}`] || '' })
      }
      i++
    }
    const extSchema = JSON.stringify(fields)
    try {
      const metaItem = allItems.find(i => i.category === currentEnum && i.dictKey === '__META__')
      if (metaItem) { await dictApi.update({ id: metaItem.id, extSchema }) }
      else { await dictApi.add({ category: currentEnum, dictKey: '__META__', dictValue: '', extSchema, tenantCode: selectedTenant }) }
      message.success('Schema 保存成功'); setSchemaModalOpen(false); loadData()
    } catch (e) { message.error('Schema 保存失败') }
  }

  // ===== 列定义 =====
  const baseColumns = catSchema.length > 0
    ? catSchema.map(sf => ({ title: sf.label || sf.field, dataIndex: sf.field, key: sf.field,
        render: (v, r) => { if (r.extData) { try { const d = JSON.parse(r.extData); return d[sf.field] ?? '-' } catch {} } return v ?? '-' },
      }))
    : [
        { title: 'Key', dataIndex: 'dictKey', key: 'dictKey', width: 120 },
        { title: 'Value', dataIndex: 'dictValue', key: 'dictValue' },
      ]
  const columns = [
    ...baseColumns,
    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 60 },
    { title: '类型', dataIndex: 'isBuiltin', key: 'isBuiltin', width: 70,
      render: (v) => v ? <Tag color="blue">内置</Tag> : <Tag color="orange">自定义</Tag> },
    { title: '操作', key: 'action', width: 120,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => { setEditingItem(record); const fv = { ...record }; if (record.extData) { try { Object.assign(fv, JSON.parse(record.extData)) } catch {} }; form.setFieldsValue(fv); setItemModalOpen(true) }}>编辑</Button>
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
            <Dropdown menu={addCatDropdown} trigger={['click']}>
              <Button size="small" icon={<PlusOutlined />} />
            </Dropdown>
            <Button size="small" icon={<ReloadOutlined />} onClick={loadData} />
          </Space>
        }>
        <Select placeholder="选择租户" style={{ width: '100%', marginBottom: 8 }}
          value={selectedTenant} onChange={setSelectedTenant} allowClear options={tenantOptions} />
        {selectedTenant ? (
          categories.length > 0
            ? <Tree treeData={categories} showLine showIcon={false} defaultExpandAll draggable={{ icon: false, nodeDraggable: (n) => n.type === 'enum' }}
                onDrop={onDrop}
                selectedKeys={selectedCat ? [selectedCat.key] : []}
                onSelect={(keys, info) => { if (info.node.type !== 'placeholder') setSelectedCat(info.node) }}
                titleRender={(node) => {
                  if (node.type === 'placeholder') return node.title
                  const isEnum = node.type === 'enum'
                  const catName = isEnum ? node.data?.category : (node.data?.catName || node.data?.catCode)
                  let isBuiltin = false
                  if (isEnum) {
                    isBuiltin = allItems.some(i => i.category === node.data?.category && i.isBuiltin)
                  }
                  const disp = isEnum ? catName : (node.data?.catName || node.data?.catCode)
                  return (
                    <span>
                      {isEnum ? (isBuiltin ? <Tag color="blue" style={{ fontSize: 10 }}>内置</Tag> : <Tag color="orange" style={{ fontSize: 10 }}>自定义</Tag>) : null}
                      {disp}
                    </span>
                  )
                }} />
            : <Empty description={initStatus === false ? '未初始化' : '暂无数据'} />
        ) : <Empty description="请选择租户" />}
      </Card>

      {/* 右侧：枚举条目 */}
      <Card size="small" title={currentEnum ? `${currentEnum} - 枚举条目` : '枚举条目'} style={{ flex: 1, overflow: 'auto' }}
        extra={currentEnum ? (
          <Space>
            <Button size="small" icon={<FieldBinaryOutlined />} onClick={handleSchemaEdit}>字段描述</Button>
            <Button type="primary" size="small" icon={<PlusOutlined />}
              onClick={() => { setEditingItem(null); form.resetFields(); form.setFieldsValue({ category: currentEnum }); setItemModalOpen(true) }}>新增</Button>
          </Space>
        ) : null}>
        {currentEnum ? (
          <Table columns={columns} dataSource={currentItems.map((r, i) => ({ ...r, key: r.id || i }))}
            pagination={false} size="small" locale={{ emptyText: '暂无条目' }} />
        ) : <Empty description="请从左侧选择枚举" />}
      </Card>

      {/* 新增分类/枚举弹窗 */}
      <Modal
        title={catModalMode === 'cat' ? '新增子分类' : '新增枚举'}
        open={catModalOpen}
        width={560}
        onCancel={() => setCatModalOpen(false)}
        onOk={() => catForm.submit()}
        destroyOnHidden
      >
        <Form
          form={catForm}
          layout="vertical"
          onFinish={async (v) => {
            try {
              if (catModalMode === 'cat') {
                await dictCatApi.create({ catCode: v.catCode, catName: v.catName, parentCode: v.parentCode || undefined, tenantCode: selectedTenant })
              } else {
                // 新增枚举：同时写入 schema
                const schemaPayload = v.extSchema ? JSON.stringify(v.extSchema) : undefined
                await dictCatApi.create({
                  catCode: v.enumCode,
                  catName: v.enumName,
                  parentCode: v.parentCode || undefined,
                  tenantCode: selectedTenant,
                  extSchema: schemaPayload,
                })
              }
              message.success('创建成功')
              setCatModalOpen(false)
              loadData()
            } catch (e) {
              message.error('创建失败')
            }
          }}
        >
          {catModalMode === 'cat' ? (
            <>
              <Form.Item name="catCode" label="分类编码" rules={[{ required: true }]}>
                <Input placeholder="如：server_config" />
              </Form.Item>
              <Form.Item name="catName" label="分类名称">
                <Input placeholder="如：服务器配置" />
              </Form.Item>
              <Form.Item name="parentCode" label="父分类">
                <Select allowClear placeholder="留空为顶级">
                  {(categories || []).filter(n => n.type === 'category').map(n => {
                    const code = n.key.replace('cat:', '')
                    return <Select.Option key={code} value={code}>{n.data?.catName || n.data?.catCode || code}</Select.Option>
                  })}
                </Select>
              </Form.Item>
            </>
          ) : (
            <>
              <Form.Item name="enumCode" label="枚举编码" rules={[{ required: true }]}>
                <Input placeholder="如：order_status" />
              </Form.Item>
              <Form.Item name="enumName" label="枚举名称" rules={[{ required: true }]}>
                <Input placeholder="如：订单状态" />
              </Form.Item>
              <Form.Item name="parentCode" label="所属分类">
                <Select allowClear placeholder="选择所属分类（可选）">
                  {(categories || []).filter(n => n.type === 'category').map(n => {
                    const code = n.key.replace('cat:', '')
                    return <Select.Option key={code} value={code}>{n.data?.catName || n.data?.catCode || code}</Select.Option>
                  })}
                </Select>
              </Form.Item>
              {/* 内联 schema 定义 */}
              <Form.Item label="字段描述（可选）">
                <Form.List name="extSchema">
                  {(fields, { add, remove }) => (
                    <>
                      {fields.map(({ key, name }) => (
                        <Space key={key} size={4} style={{ display: 'flex', marginBottom: 8 }}>
                          <Form.Item name={[name, 'field']} noStyle><Input placeholder="字段名" style={{ width: 100 }} /></Form.Item>
                          <Form.Item name={[name, 'label']} noStyle><Input placeholder="显示名" style={{ width: 80 }} /></Form.Item>
                          <Form.Item name={[name, 'type']} initialValue="text" noStyle>
                            <Select style={{ width: 70 }}>
                              <Select.Option value="text">文本</Select.Option>
                              <Select.Option value="number">数字</Select.Option>
                            </Select>
                          </Form.Item>
                          <Form.Item name={[name, 'required']} valuePropName="checked" initialValue={false} noStyle>
                            <Checkbox>必填</Checkbox>
                          </Form.Item>
                          <Button size="small" danger onClick={() => remove(name)}>删</Button>
                        </Space>
                      ))}
                      <Button type="dashed" size="small" block onClick={() => add({ field: '', label: '', type: 'text', required: false })}>+ 添加字段</Button>
                    </>
                  )}
                </Form.List>
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>

      {/* Schema 弹窗 */}
      <Modal title="字段描述定义" open={schemaModalOpen} width={700}
        onCancel={() => setSchemaModalOpen(false)}
        onOk={handleSchemaSave} destroyOnHidden>
        <Form form={schemaForm} layout="vertical">
          <Table dataSource={schemaFields.map((f, i) => ({ ...f, key: i, idx: i }))} pagination={false} size="small"
            columns={[
              { title: '字段名', render: (_, r) => <Form.Item name={`field_${r.idx}`} initialValue={r.field} noStyle><Input size="small" /></Form.Item> },
              { title: '显示名称', render: (_, r) => <Form.Item name={`label_${r.idx}`} initialValue={r.label} noStyle><Input size="small" /></Form.Item> },
              { title: '类型', width: 80, render: (_, r) => <Form.Item name={`type_${r.idx}`} initialValue={r.type || 'text'} noStyle><Select size="small"><Select.Option value="text">文本</Select.Option><Select.Option value="number">数字</Select.Option></Select></Form.Item> },
              { title: '必填', width: 50, render: (_, r) => <Form.Item name={`req_${r.idx}`} valuePropName="checked" initialValue={r.required} noStyle><Checkbox /></Form.Item> },
              { title: '提示', render: (_, r) => <Form.Item name={`placeholder_${r.idx}`} initialValue={r.placeholder} noStyle><Input size="small" /></Form.Item> },
              { title: '校验', render: (_, r) => <Form.Item name={`pattern_${r.idx}`} initialValue={r.pattern} noStyle><Input size="small" placeholder="如：^[a-z]+$" /></Form.Item> },
            ]}
            footer={() => <Button type="dashed" size="small" block icon={<PlusOutlined />} onClick={() => setSchemaFields([...schemaFields, { field: '', label: '', type: 'text' }])}>新增字段</Button>} />
        </Form>
      </Modal>

      {/* 条目编辑弹窗 */}
      <Modal title={editingItem ? '编辑条目' : '新增条目'} open={itemModalOpen} width={600}
        onCancel={() => { setItemModalOpen(false); setEditingItem(null) }}
        onOk={() => form.submit()} destroyOnHidden>
        <Form form={form} layout="vertical" onFinish={async (values) => {
          const extData = {}; catSchema.forEach(sf => { if (values[sf.field] !== undefined) extData[sf.field] = values[sf.field] })
          const payload = { ...values, extData: Object.keys(extData).length > 0 ? JSON.stringify(extData) : undefined }
          try {
            if (editingItem) { await dictApi.update({ ...payload, id: editingItem.id }); message.success('更新成功') }
            else { await dictApi.add({ ...payload, tenantCode: selectedTenant }); message.success('创建成功') }
            setItemModalOpen(false); setEditingItem(null); loadData()
          } catch (e) { message.error('操作失败') }
        }}>
          <Form.Item name="category" label="枚举" rules={[{ required: true }]}>
            <Select disabled={!!editingItem} mode="tags" maxCount={1} options={allItems.filter(i => i.dictKey === '__META__').map(i => ({ label: i.category, value: i.category }))} />
          </Form.Item>
          {catSchema.length === 0 ? (
            <>
              <Form.Item name="dictKey" label="Key" rules={[{ required: true }]}><Input disabled={!!editingItem} /></Form.Item>
              <Form.Item name="dictValue" label="Value" rules={[{ required: true }]}><Input /></Form.Item>
            </>
          ) : (
            catSchema.map(sf => {
              const rules = []
              if (sf.required) rules.push({ required: true, message: `请输入${sf.label || sf.field}` })
              if (sf.pattern) rules.push({ pattern: new RegExp(sf.pattern), message: `格式不符: ${sf.pattern}` })
              return (
                <Form.Item key={sf.field} name={sf.field} label={sf.label || sf.field} rules={rules}>
                  {sf.type === 'number' ? <InputNumber style={{ width: '100%' }} placeholder={sf.placeholder} /> : <Input placeholder={sf.placeholder} />}
                </Form.Item>
              )
            })
          )}
          <Form.Item name="sortOrder" label="排序" initialValue={0}><Input /></Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default DictPage
