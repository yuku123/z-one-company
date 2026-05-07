import { useState, useEffect, useCallback } from 'react'
import { Card, Tree, Table, Button, Space, Tag, Descriptions, Modal, Form, Input, message, Popconfirm, Empty } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons'
import { getTenantList, updateTenant } from '@/services/api'
import { getDomainList, updateDomain } from '@/services/api'

const TenantManage = () => {
  const [loading, setLoading] = useState(false)
  const [treeData, setTreeData] = useState([])
  const [selectedNode, setSelectedNode] = useState(null)
  const [selectedType, setSelectedType] = useState(null)
  const [configData, setConfigData] = useState([])
  const [modalOpen, setModalOpen] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [form] = Form.useForm()
  const [tenantMap, setTenantMap] = useState({})
  const [domainMap, setDomainMap] = useState({})

  // 解析 extConfig JSON → K-V 数组
  const parseExtConfig = (extConfig) => {
    if (!extConfig) return []
    try { return JSON.parse(extConfig) } catch { return [] }
  }

  // K-V 数组 → JSON 字符串
  const toExtConfig = (kvList) => JSON.stringify(kvList)

  const buildTree = useCallback(async () => {
    setLoading(true)
    try {
      const tenants = await getTenantList()
      const domains = await getDomainList()
      const tMap = {}; const dMap = {}
      tenants?.forEach(t => { tMap[t.tenantCode] = t })

      const tree = (tenants || []).map(t => {
        const childDomains = (domains || []).filter(d => d.tenantCode === t.tenantCode)
        childDomains.forEach(d => { dMap[d.domainCode] = d })
        return {
          key: `tenant:${t.tenantCode}`,
          title: `${t.tenantName || t.tenantCode} (${t.tenantCode})`,
          type: 'tenant',
          data: t,
          children: childDomains.map(d => ({
            key: `domain:${d.domainCode}`,
            title: `${d.domainName || d.domainCode} (${d.domainCode})`,
            type: 'domain',
            data: d,
            isLeaf: true,
          })),
        }
      })
      setTreeData(tree)
      setTenantMap(tMap)
      setDomainMap(dMap)
    } catch (e) {
      message.error('加载租户/域数据失败')
    } finally { setLoading(false) }
  }, [])

  useEffect(() => { buildTree() }, [buildTree])

  const onSelect = (keys, info) => {
    if (keys.length === 0) return
    const node = info.node
    setSelectedNode(node.data)
    setSelectedType(node.type)
    setConfigData(parseExtConfig(node.data.extConfig))
  }

  // 保存 K-V 到实体 extConfig 字段
  const saveExtConfig = async (kvList) => {
    const data = { ...selectedNode }
    data.extConfig = toExtConfig(kvList)
    const api = selectedType === 'tenant' ? updateTenant : updateDomain
    await api(data)
    // 刷新节点数据
    const key = selectedType === 'tenant'
      ? `tenant:${selectedNode.tenantCode}`
      : `domain:${selectedNode.domainCode}`
    const newData = { ...selectedNode, extConfig: data.extConfig }
    setSelectedNode(newData)
    setConfigData(kvList)
    // 更新树缓存
    if (selectedType === 'tenant') {
      setTenantMap(prev => ({ ...prev, [selectedNode.tenantCode]: newData }))
    } else {
      setDomainMap(prev => ({ ...prev, [selectedNode.domainCode]: newData }))
    }
  }

  const handleSaveConfig = async (values) => {
    let kvList
    if (editingRecord) {
      kvList = configData.map(r => r.key === editingRecord.key ? { key: values.dataId, value: values.content } : r)
    } else {
      kvList = [...configData, { key: values.dataId, value: values.content }]
    }
    try {
      await saveExtConfig(kvList)
      message.success(editingRecord ? '更新成功' : '创建成功')
      setModalOpen(false); setEditingRecord(null); form.resetFields()
    } catch (e) { message.error('保存失败') }
  }

  const handleDeleteConfig = async (record) => {
    const kvList = configData.filter(r => r.key !== record.key)
    try {
      await saveExtConfig(kvList)
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
            onClick={() => { setEditingRecord(record); form.setFieldsValue({ dataId: record.key, content: record.value }); setModalOpen(true) }}>编辑</Button>
          <Popconfirm title="确认删除" onConfirm={() => handleDeleteConfig(record)}>
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      {/* 左侧树 */}
      <Card title="租户 / 域" size="small" style={{ width: 280, overflow: 'auto', flexShrink: 0 }}
        extra={<Button size="small" icon={<ReloadOutlined />} onClick={buildTree} />}>
        {treeData.length > 0
          ? <Tree treeData={treeData} onSelect={onSelect} defaultExpandAll showLine />
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
                  onClick={() => { setEditingRecord(null); form.resetFields(); setModalOpen(true) }}>
                  新增
                </Button>
              }>
              <Table columns={configColumns} dataSource={configData.map((r, i) => ({ ...r, key: r.key || i }))}
                pagination={false} size="small" locale={{ emptyText: '暂无扩展配置' }} />
            </Card>
          </>
        )}
      </Card>

      <Modal title={editingRecord ? '编辑配置' : '新增配置'}
        open={modalOpen} onCancel={() => { setModalOpen(false); setEditingRecord(null) }}
        onOk={() => form.submit()} destroyOnClose>
        <Form form={form} layout="vertical" onFinish={handleSaveConfig}>
          <Form.Item name="dataId" label="Key" rules={[{ required: true }]}>
            <Input disabled={!!editingRecord} placeholder="如：max_users" />
          </Form.Item>
          <Form.Item name="content" label="Value" rules={[{ required: true }]}>
            <Input.TextArea rows={4} placeholder="配置值" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default TenantManage
