import { useState, useEffect, useCallback } from 'react'
import { Card, Tree, Table, Button, Space, Tag, Descriptions, Modal, Form, Input, message, Popconfirm, Empty } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, ApartmentOutlined } from '@ant-design/icons'
import { getTenantList, updateTenant, createTenant, deleteTenant } from '@/services/api'
import { getDomainList, updateDomain, createDomain, deleteDomain } from '@/services/api'

const TenantManage = () => {
  const [loading, setLoading] = useState(false)
  const [treeData, setTreeData] = useState([])
  const [selectedNode, setSelectedNode] = useState(null)
  const [selectedType, setSelectedType] = useState(null)
  const [configData, setConfigData] = useState([])
  const [configModalOpen, setConfigModalOpen] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [kvForm] = Form.useForm()
  const [tenantMap, setTenantMap] = useState({})
  const [domainMap, setDomainMap] = useState({})

  // 新增租户/域弹窗
  const [entityModalOpen, setEntityModalOpen] = useState(false)
  const [entityType, setEntityType] = useState(null) // 'tenant' | 'domain'
  const [entityParentTenant, setEntityParentTenant] = useState(null) // for domain
  const [entityForm] = Form.useForm()

  const parseExtConfig = (extConfig) => {
    if (!extConfig) return []
    try { return JSON.parse(extConfig) } catch { return [] }
  }
  const toExtConfig = (kvList) => JSON.stringify(kvList)

  // ========== 树数据 ==========
  const buildTree = useCallback(async () => {
    setLoading(true)
    try {
      const tenants = await getTenantList()
      const domains = await getDomainList()
      const tMap = {}; const dMap = {}
      tenants?.forEach(t => { tMap[t.tenantCode] = t })
      domains?.forEach(d => { dMap[d.domainCode] = d })

      const tree = (tenants || []).map(t => ({
        key: `tenant:${t.tenantCode}`,
        title: `${t.tenantName || t.tenantCode}`,
        type: 'tenant',
        data: t,
        children: (domains || []).filter(d => d.tenantCode === t.tenantCode).map(d => ({
          key: `domain:${d.domainCode}`,
          title: d.domainName || d.domainCode,
          type: 'domain',
          data: d,
          isLeaf: true,
        })),
      }))
      setTreeData(tree)
      setTenantMap(tMap)
      setDomainMap(dMap)
    } catch (e) { message.error('加载失败') } finally { setLoading(false) }
  }, [])

  useEffect(() => { buildTree() }, [buildTree])

  // ========== 树节点渲染（标题+操作按钮） ==========
  const titleRender = (nodeData) => {
    const { title, type, data } = nodeData
    const label = type === 'tenant'
      ? `${title} (${data.tenantCode})`
      : `${title} (${data.domainCode})`

    return (
      <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
        <span style={{ flex: 1 }}>{label}</span>
        {/* 租户节点：新增域 */}
        {type === 'tenant' && (
          <Button type="text" size="small" icon={<PlusOutlined style={{ fontSize: 12, color: '#1890ff' }} />}
            onClick={(e) => { e.stopPropagation(); openEntityModal('domain', data.tenantCode) }}
            title="新增域" />
        )}
        {/* 删除按钮 */}
        <Popconfirm
          title={type === 'tenant' ? `确定删除租户 "${data.tenantCode}"？` : `确定删除域 "${data.domainCode}"？`}
          description="此操作不可恢复"
          onConfirm={(e) => { e?.stopPropagation(); handleDeleteEntity(type, data) }}
          onCancel={(e) => e?.stopPropagation()}
          okText="确定" cancelText="取消">
          <Button type="text" size="small" danger
            icon={<DeleteOutlined style={{ fontSize: 12 }} />}
            onClick={(e) => e.stopPropagation()} />
        </Popconfirm>
      </span>
    )
  }

  // ========== 删除实体 ==========
  const handleDeleteEntity = async (type, data) => {
    try {
      if (type === 'tenant') {
        await deleteTenant(data.tenantCode)
      } else {
        await deleteDomain(data.domainCode)
      }
      message.success('删除成功')
      if (selectedNode && (
        (type === 'tenant' && selectedType === 'tenant' && selectedNode.tenantCode === data.tenantCode) ||
        (type === 'domain' && selectedType === 'domain' && selectedNode.domainCode === data.domainCode)
      )) {
        setSelectedNode(null); setSelectedType(null); setConfigData([])
      }
      buildTree()
    } catch (e) { message.error('删除失败') }
  }

  // ========== 新增租户/域弹窗 ==========
  const openEntityModal = (type, parentTenant = null) => {
    setEntityType(type)
    setEntityParentTenant(parentTenant)
    entityForm.resetFields()
    if (type === 'domain' && parentTenant) {
      entityForm.setFieldsValue({ tenantCode: parentTenant })
    }
    setEntityModalOpen(true)
  }

  const handleEntitySave = async (values) => {
    try {
      if (entityType === 'tenant') {
        await createTenant(values)
      } else {
        await createDomain(values)
      }
      message.success('创建成功')
      setEntityModalOpen(false)
      buildTree()
    } catch (e) { message.error('创建失败') }
  }

  // ========== 树选择 ==========
  const onSelect = (keys, info) => {
    if (keys.length === 0) return
    const node = info.node
    setSelectedNode(node.data)
    setSelectedType(node.type)
    setConfigData(parseExtConfig(node.data.extConfig))
  }

  // ========== K-V 配置 ==========
  const saveExtConfig = async (kvList) => {
    const data = { ...selectedNode }
    data.extConfig = toExtConfig(kvList)
    const api = selectedType === 'tenant' ? updateTenant : updateDomain
    await api(data)
    const newData = { ...selectedNode, extConfig: data.extConfig }
    setSelectedNode(newData)
    setConfigData(kvList)
    if (selectedType === 'tenant') {
      setTenantMap(prev => ({ ...prev, [selectedNode.tenantCode]: newData }))
    } else {
      setDomainMap(prev => ({ ...prev, [selectedNode.domainCode]: newData }))
    }
  }

  const handleSaveConfig = async (values) => {
    const kvList = editingRecord
      ? configData.map(r => r.key === editingRecord.key ? { key: values.dataId, value: values.content } : r)
      : [...configData, { key: values.dataId, value: values.content }]
    try {
      await saveExtConfig(kvList)
      message.success(editingRecord ? '更新成功' : '创建成功')
      setConfigModalOpen(false); setEditingRecord(null); kvForm.resetFields()
    } catch (e) { message.error('保存失败') }
  }

  const handleDeleteConfig = async (record) => {
    try {
      await saveExtConfig(configData.filter(r => r.key !== record.key))
      message.success('删除成功')
    } catch (e) { message.error('删除失败') }
  }

  const configColumns = [
    { title: 'Key', dataIndex: 'key', key: 'key', width: 200 },
    { title: 'Value', dataIndex: 'value', key: 'value', ellipsis: true },
    { title: '操作', key: 'action', width: 120,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => { setEditingRecord(record); kvForm.setFieldsValue({ dataId: record.key, content: record.value }); setConfigModalOpen(true) }}>编辑</Button>
          <Popconfirm title="确认删除" onConfirm={() => handleDeleteConfig(record)}>
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  // ========== 渲染 ==========
  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      {/* 左侧树 */}
      <Card size="small" style={{ width: 300, overflow: 'auto', flexShrink: 0 }}
        title="租户 / 域"
        extra={
          <Space size={4}>
            <Button size="small" icon={<PlusOutlined />} onClick={() => openEntityModal('tenant')}>新增租户</Button>
            <Button size="small" icon={<ReloadOutlined />} onClick={buildTree} />
          </Space>
        }>
        {treeData.length > 0
          ? <Tree treeData={treeData} onSelect={onSelect} defaultExpandAll showLine
              titleRender={titleRender} blockNode />
          : <Empty description={loading ? '加载中...' : '暂无数据'} />}
      </Card>

      {/* 右侧面板 */}
      <Card size="small" style={{ flex: 1, overflow: 'auto' }}>
        {!selectedNode ? (
          <Empty description="请从左侧选择租户或域" />
        ) : (
          <>
            <Descriptions title={selectedType === 'tenant' ? '租户信息' : '域信息'}
              size="small" bordered column={2} style={{ marginBottom: 16 }}>
              {selectedType === 'tenant' ? (
                <>
                  <Descriptions.Item label="编码">{selectedNode.tenantCode}</Descriptions.Item>
                  <Descriptions.Item label="名称">{selectedNode.tenantName}</Descriptions.Item>
                  <Descriptions.Item label="联系人">{selectedNode.contactName || '-'}</Descriptions.Item>
                  <Descriptions.Item label="电话">{selectedNode.contactPhone || '-'}</Descriptions.Item>
                  <Descriptions.Item label="状态">
                    <Tag color={selectedNode.status === 1 ? 'green' : 'red'}>
                      {selectedNode.status === 1 ? '正常' : '停用'}</Tag>
                  </Descriptions.Item>
                </>
              ) : (
                <>
                  <Descriptions.Item label="编码">{selectedNode.domainCode}</Descriptions.Item>
                  <Descriptions.Item label="名称">{selectedNode.domainName}</Descriptions.Item>
                  <Descriptions.Item label="所属租户">
                    {tenantMap[selectedNode.tenantCode]?.tenantName || selectedNode.tenantCode}
                  </Descriptions.Item>
                  <Descriptions.Item label="状态">
                    <Tag color={selectedNode.status === 1 ? 'green' : 'red'}>
                      {selectedNode.status === 1 ? '正常' : '停用'}</Tag>
                  </Descriptions.Item>
                </>
              )}
            </Descriptions>

            <Card type="inner" title="扩展配置 (K-V)" size="small"
              extra={
                <Button type="primary" size="small" icon={<PlusOutlined />}
                  onClick={() => { setEditingRecord(null); kvForm.resetFields(); setConfigModalOpen(true) }}>新增</Button>
              }>
              <Table columns={configColumns} dataSource={configData.map((r, i) => ({ ...r, key: r.key || i }))}
                pagination={false} size="small" locale={{ emptyText: '暂无扩展配置' }} />
            </Card>
          </>
        )}
      </Card>

      {/* K-V 编辑弹窗 */}
      <Modal title={editingRecord ? '编辑配置' : '新增配置'}
        open={configModalOpen} onCancel={() => { setConfigModalOpen(false); setEditingRecord(null) }}
        onOk={() => kvForm.submit()} destroyOnClose>
        <Form form={kvForm} layout="vertical" onFinish={handleSaveConfig}>
          <Form.Item name="dataId" label="Key" rules={[{ required: true }]}>
            <Input disabled={!!editingRecord} placeholder="如：max_users" /></Form.Item>
          <Form.Item name="content" label="Value" rules={[{ required: true }]}>
            <Input.TextArea rows={4} placeholder="配置值" /></Form.Item>
        </Form>
      </Modal>

      {/* 新增租户/域弹窗 */}
      <Modal title={entityType === 'tenant' ? '新增租户' : '新增域'}
        open={entityModalOpen} onCancel={() => setEntityModalOpen(false)}
        onOk={() => entityForm.submit()} destroyOnClose>
        <Form form={entityForm} layout="vertical" onFinish={handleEntitySave}>
          {entityType === 'tenant' ? (
            <>
              <Form.Item name="tenantCode" label="租户编码" rules={[{ required: true }]}>
                <Input placeholder="如：acme" /></Form.Item>
              <Form.Item name="tenantName" label="租户名称" rules={[{ required: true }]}>
                <Input placeholder="如：ACME公司" /></Form.Item>
              <Form.Item name="contactName" label="联系人">
                <Input placeholder="联系人姓名" /></Form.Item>
              <Form.Item name="contactPhone" label="联系电话">
                <Input placeholder="联系电话" /></Form.Item>
              <Form.Item name="contactEmail" label="联系邮箱">
                <Input placeholder="联系邮箱" /></Form.Item>
            </>
          ) : (
            <>
              <Form.Item name="tenantCode" label="所属租户">
                <Input disabled /></Form.Item>
              <Form.Item name="domainCode" label="域编码" rules={[{ required: true }]}>
                <Input placeholder="如：prod" /></Form.Item>
              <Form.Item name="domainName" label="域名称" rules={[{ required: true }]}>
                <Input placeholder="如：生产环境" /></Form.Item>
              <Form.Item name="description" label="描述">
                <Input.TextArea rows={2} placeholder="域描述" /></Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  )
}

export default TenantManage
