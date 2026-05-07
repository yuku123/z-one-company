import { useState, useEffect, useCallback } from 'react'
import { Card, Tree, Table, Button, Space, Tag, Descriptions, Modal, Form, Input, Select, message, Popconfirm, Empty, Dropdown } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons'
import { getTenantList, getDomainByTenantCode } from '@/services/api'
import { getOrgList, createOrg, updateOrg, deleteOrg } from '@/services/api'
import { getOrgByDomainCode, getDeptByOrgCode, getDeptList, createDept, updateDept, deleteDept } from '@/services/api'
import { getGroupByDeptCode, getGroupList, createGroup, updateGroup, deleteGroup } from '@/services/api'

const OrgManage = () => {
  const [tenantOptions, setTenantOptions] = useState([])
  const [domainOptions, setDomainOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(null)
  const [selectedDomain, setSelectedDomain] = useState(null)
  const [treeLoading, setTreeLoading] = useState(false)
  const [treeData, setTreeData] = useState([])
  const [expandedKeys, setExpandedKeys] = useState([])
  const [selectedNode, setSelectedNode] = useState(null)
  const [selectedType, setSelectedType] = useState(null)
  const [childList, setChildList] = useState([])
  const [configData, setConfigData] = useState([])
  const [configModalOpen, setConfigModalOpen] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [kvForm] = Form.useForm()
  const [entityModalOpen, setEntityModalOpen] = useState(false)
  const [entityType, setEntityType] = useState(null)
  const [entityParent, setEntityParent] = useState(null)
  const [entityForm] = Form.useForm()

  const parseExtConfig = (extConfig) => {
    if (!extConfig) return []
    try { return JSON.parse(extConfig) } catch { return [] }
  }
  const toExtConfig = (kvList) => JSON.stringify(kvList)

  // 类型配置：颜色 + API
  const typeConfig = {
    org: { label: '组织', color: '#1677ff', api: { create: createOrg, update: updateOrg, delete: deleteOrg, children: getDeptByOrgCode } },
    dept: { label: '部门', color: '#52c41a', api: { create: createDept, update: updateDept, delete: deleteDept, children: getGroupByDeptCode } },
    group: { label: '组', color: '#fa8c16', api: { create: createGroup, update: updateGroup, delete: deleteGroup, children: null } },
  }

  // ========== 租户/域选择器 ==========
  useEffect(() => {
    (async () => {
      const tenants = await getTenantList()
      setTenantOptions((tenants || []).map(t => ({ label: `${t.tenantName} (${t.tenantCode})`, value: t.tenantCode })))
      // 自动读取全局选择的租户域
      const savedTenant = localStorage.getItem('z_tenant')
      const savedDomain = localStorage.getItem('z_domain')
      if (savedTenant) {
        setSelectedTenant(savedTenant)
        const domains = await getDomainByTenantCode(savedTenant)
        setDomainOptions((domains || []).map(d => ({ label: `${d.domainName} (${d.domainCode})`, value: d.domainCode })))
        if (savedDomain) setSelectedDomain(savedDomain)
      }
    })()
  }, [])

  const onTenantChange = async (code) => {
    setSelectedTenant(code)
    setSelectedDomain(null)
    setTreeData([])
    if (!code) return
    const domains = await getDomainByTenantCode(code)
    setDomainOptions((domains || []).map(d => ({ label: `${d.domainName} (${d.domainCode})`, value: d.domainCode })))
  }

  // ========== 树加载 ==========
  const loadTree = useCallback(async (tenant, domain) => {
    if (!domain) return
    setTreeLoading(true)
    try {
      const orgs = await getOrgByDomainCode(domain)
      const tree = (orgs || []).map(o => ({
        key: `org:${o.orgCode}`,
        title: `${o.orgName || o.orgCode}`,
        type: 'org',
        data: o,
        isLeaf: false,
      }))
      setTreeData(tree)
      // keep expanded keys that are still valid org nodes
      setExpandedKeys(prev => {
        const valid = tree.map(t => t.key)
        return prev.filter(k => valid.includes(k))
      })
    } catch (e) { message.error('加载组织失败') } finally { setTreeLoading(false) }
  }, [])

  useEffect(() => {
    if (selectedTenant && selectedDomain) {
      setExpandedKeys([]) // fresh domain, reset expands
      loadTree(selectedTenant, selectedDomain)
    }
  }, [selectedTenant, selectedDomain, loadTree])

  // 懒加载子节点
  const onLoadData = async (node) => {
    const { type, data } = node
    const cfg = typeConfig[type]
    if (!cfg || !cfg.api.children) return

    let children
    if (type === 'org') {
      children = await cfg.api.children(data.orgCode)
      children = (children || []).map(d => ({
        key: `dept:${d.deptCode}`,
        title: `${d.deptName || d.deptCode}`,
        type: 'dept',
        data: d,
        isLeaf: false,
      }))
    } else if (type === 'dept') {
      children = await cfg.api.children(data.deptCode)
      children = (children || []).map(g => ({
        key: `group:${g.groupCode}`,
        title: `${g.groupName || g.groupCode}`,
        type: 'group',
        data: g,
        isLeaf: true,
      }))
    }
    if (children) {
      setTreeData(prev => updateTreeData(prev, node.key, children))
    }
  }

  // 递归更新树节点
  const updateTreeData = (list, key, children) =>
    list.map(item => {
      if (item.key === key) return { ...item, children }
      if (item.children) return { ...item, children: updateTreeData(item.children, key, children) }
      return item
    })

  // ========== 树标题渲染 ==========
  const titleRender = (nodeData) => {
    const { title, type, data } = nodeData
    const cfg = typeConfig[type]
    const addMenuItems = type === 'org'
      ? [
          { key: 'org', label: <><Tag color="#1677ff">组织</Tag>新增组织</> },
          { key: 'dept', label: <><Tag color="#52c41a">部门</Tag>新增部门</> },
          { key: 'group', label: <><Tag color="#fa8c16">组</Tag>新增组别</> },
        ]
      : type === 'dept'
      ? [
          { key: 'dept', label: <><Tag color="#52c41a">部门</Tag>新增部门</> },
          { key: 'group', label: <><Tag color="#fa8c16">组</Tag>新增组别</> },
        ]
      : []
    return (
      <span style={{ display: 'flex', alignItems: 'center', gap: 6, width: '100%' }}>
        <Tag color={cfg.color} style={{ margin: 0 }}>{cfg.label}</Tag>
        <span style={{ flex: 1 }}>{title}</span>
        {addMenuItems.length > 0 && (
          <Dropdown menu={{ items: addMenuItems, onClick: ({ key }) => { openEntityModal(key, data) } }}
            trigger={['click']}>
            <Button type="text" size="small" className="org-tree-btn"
              icon={<PlusOutlined style={{ fontSize: 11 }} />}
              onClick={(e) => e.stopPropagation()} />
          </Dropdown>
        )}
        <Popconfirm title={`确定删除${cfg.label} "${title}"？`} description="此操作不可恢复"
          onConfirm={(e) => { e?.stopPropagation(); handleDeleteEntity(type, data) }}
          onCancel={(e) => e?.stopPropagation()} okText="确定" cancelText="取消">
          <Button type="text" size="small" danger icon={<DeleteOutlined style={{ fontSize: 11 }} />}
            onClick={(e) => e.stopPropagation()} />
        </Popconfirm>
      </span>
    )
  }

  // ========== 删除实体 ==========
  const handleDeleteEntity = async (type, data) => {
    try {
      await typeConfig[type].api.delete(type === 'org' ? data.orgCode : type === 'dept' ? data.deptCode : data.groupCode)
      message.success('删除成功')
      if (selectedNode && selectedNode[`${type}Code`] === data[`${type}Code`]) {
        setSelectedNode(null); setSelectedType(null); setChildList([]); setConfigData([])
      }
      loadTree(selectedTenant, selectedDomain)
    } catch (e) { message.error('删除失败') }
  }

  // ========== 新增实体 ==========
  const openEntityModal = (type, parent) => {
    setEntityType(type)
    setEntityParent(parent)
    entityForm.resetFields()
    if (type === 'org') {
      entityForm.setFieldsValue({
        tenantCode: parent.tenantCode,
        domainCode: parent.domainCode,
        parentCode: parent.orgCode,
      })
    } else if (type === 'dept') {
      entityForm.setFieldsValue({
        tenantCode: parent.tenantCode,
        domainCode: parent.domainCode,
        orgCode: parent.orgCode || parent.deptCode ? parent.orgCode : undefined,
        parentCode: parent.deptCode || undefined,
      })
    } else if (type === 'group') {
      entityForm.setFieldsValue({
        tenantCode: parent.tenantCode,
        domainCode: parent.domainCode,
        orgCode: parent.orgCode,
        deptCode: parent.deptCode,
      })
    }
    setEntityModalOpen(true)
  }

  const handleEntitySave = async (values) => {
    try {
      await typeConfig[entityType].api.create(values)
      message.success('创建成功')
      setEntityModalOpen(false)
      loadTree(selectedTenant, selectedDomain)
    } catch (e) { message.error('创建失败') }
  }

  // ========== 节点选择 → 右侧面板 ==========
  const onSelect = async (keys, info) => {
    if (keys.length === 0) return
    const node = info.node
    setSelectedNode(node.data)
    setSelectedType(node.type)
    setConfigData(parseExtConfig(node.data.extConfig))

    // 加载子列表
    const cfg = typeConfig[node.type]
    if (cfg.api.children) {
      try {
        const code = node.type === 'org' ? node.data.orgCode : node.data.deptCode
        const children = await cfg.api.children(code)
        setChildList(children || [])
      } catch (e) { setChildList([]) }
    } else {
      setChildList([])
    }
  }

  // ========== K-V 配置 ==========
  const saveExtConfig = async (kvList) => {
    const data = { ...selectedNode }
    data.extConfig = toExtConfig(kvList)
    await typeConfig[selectedType].api.update(data)
    setSelectedNode({ ...selectedNode, extConfig: data.extConfig })
    setConfigData(kvList)
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
    try { await saveExtConfig(configData.filter(r => r.key !== record.key)); message.success('删除成功') }
    catch (e) { message.error('删除失败') }
  }

  // 子列表点击 → 展开树节点
  const handleChildClick = (type, record) => {
    const codeField = type === 'org' ? 'orgCode' : type === 'dept' ? 'deptCode' : 'groupCode'
    const targetKey = `${type}:${record[codeField]}`
    setSelectedNode(record)
    setSelectedType(type)
    setConfigData(parseExtConfig(record.extConfig))
    // 尝试加载子列表
    if (type !== 'group') {
      const cfg = typeConfig[type]
      cfg.api.children(type === 'org' ? record.orgCode : record.deptCode).then(c => setChildList(c || []))
    } else {
      setChildList([])
    }
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

  const childColumns = [
    { title: '编码', dataIndex: 'orgCode', key: 'orgCode', render: (_, r) => r.orgCode || r.deptCode || r.groupCode },
    { title: '名称', dataIndex: 'orgName', key: 'orgName', render: (_, r) => r.orgName || r.deptName || r.groupName },
    { title: '类型', key: 'type',
      render: (_, r) => {
        if (r.orgCode !== undefined && !r.deptCode) return <Tag color="#1677ff">组织</Tag>
        if (r.deptCode !== undefined && !r.groupCode) return <Tag color="#52c41a">部门</Tag>
        return <Tag color="#fa8c16">组</Tag>
      },
    },
    { title: '操作', key: 'action',
      render: (_, r) => {
        let type, code
        if (r.deptCode && !r.groupCode) { type = 'dept'; code = r.deptCode } // org's child: dept
        else if (r.groupCode) { type = 'group'; code = r.groupCode } // dept's child: group
        else return null // shouldn't happen
        return <Button type="link" size="small" icon={<EditOutlined />}
          onClick={() => handleChildClick(type, r)}>详情</Button>
      },
    },
  ]

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      <style>{`
        .ant-tree-treenode-selected .org-tree-btn,
        .ant-tree-treenode-selected .org-tree-btn:hover { color: #fff !important; }
      `}</style>
      {/* 左侧 */}
      <Card size="small" style={{ width: 340, overflow: 'auto', flexShrink: 0 }}
        title="组织管理"
        extra={
          <Space size={4}>
            {selectedDomain && <Button size="small" icon={<PlusOutlined />} onClick={() => openEntityModal('org', null)}>新增组织</Button>}
            <Button size="small" icon={<ReloadOutlined />} onClick={() => loadTree(selectedTenant, selectedDomain)} />
          </Space>
        }>
        <div style={{ marginBottom: 8 }}>
          <Select placeholder="选择租户" style={{ width: '100%', marginBottom: 4 }}
            value={selectedTenant} onChange={onTenantChange} allowClear options={tenantOptions} />
          <Select placeholder="选择域" style={{ width: '100%' }}
            value={selectedDomain} onChange={setSelectedDomain} allowClear options={domainOptions}
            disabled={!selectedTenant} />
        </div>
        {selectedDomain ? (
          treeData.length > 0
            ? <Tree.DirectoryTree treeData={treeData} onSelect={onSelect} loadData={onLoadData}
                titleRender={titleRender} blockNode showLine showIcon={false}
                expandedKeys={expandedKeys} onExpand={setExpandedKeys} />
            : <Empty description={treeLoading ? '加载中...' : '暂无组织'} />
        ) : (
          <Empty description="请先选择租户和域" />
        )}
      </Card>

      {/* 右侧 */}
      <Card size="small" style={{ flex: 1, overflow: 'auto' }}>
        {!selectedNode ? (
          <Empty description="请从左侧选择节点" />
        ) : (
          <>
            <Descriptions title={`${typeConfig[selectedType].label}信息`} size="small" bordered column={2} style={{ marginBottom: 16 }}>
              <Descriptions.Item label="编码">
                {selectedNode.orgCode || selectedNode.deptCode || selectedNode.groupCode}</Descriptions.Item>
              <Descriptions.Item label="名称">
                {selectedNode.orgName || selectedNode.deptName || selectedNode.groupName}</Descriptions.Item>
              <Descriptions.Item label="描述">{selectedNode.description || '-'}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={selectedNode.status === 1 ? 'green' : 'red'}>
                  {selectedNode.status === 1 ? '正常' : '停用'}</Tag>
              </Descriptions.Item>
            </Descriptions>

            {/* 子列表 */}
            {selectedType !== 'group' && (
              <Card type="inner" title={`下级${selectedType === 'org' ? '部门/组' : '组'}`} size="small" style={{ marginBottom: 16 }}>
                <Table columns={childColumns} dataSource={childList.map((r, i) => ({ ...r, key: i }))}
                  pagination={false} size="small" locale={{ emptyText: '暂无子节点' }} />
              </Card>
            )}

            {/* K-V 配置 */}
            <Card type="inner" title="扩展配置 (K-V)" size="small"
              extra={<Button type="primary" size="small" icon={<PlusOutlined />}
                onClick={() => { setEditingRecord(null); kvForm.resetFields(); setConfigModalOpen(true) }}>新增</Button>}>
              <Table columns={configColumns} dataSource={configData.map((r, i) => ({ ...r, key: r.key || i }))}
                pagination={false} size="small" locale={{ emptyText: '暂无扩展配置' }} />
            </Card>
          </>
        )}
      </Card>

      {/* K-V 弹窗 */}
      <Modal title={editingRecord ? '编辑配置' : '新增配置'}
        open={configModalOpen} onCancel={() => { setConfigModalOpen(false); setEditingRecord(null) }}
        onOk={() => kvForm.submit()} destroyOnClose>
        <Form form={kvForm} layout="vertical" onFinish={handleSaveConfig}>
          <Form.Item name="dataId" label="Key" rules={[{ required: true }]}>
            <Input disabled={!!editingRecord} /></Form.Item>
          <Form.Item name="content" label="Value" rules={[{ required: true }]}>
            <Input.TextArea rows={4} /></Form.Item>
        </Form>
      </Modal>

      {/* 新增实体弹窗 */}
      <Modal title={`新增${typeConfig[entityType]?.label || ''}`}
        open={entityModalOpen} onCancel={() => setEntityModalOpen(false)}
        onOk={() => entityForm.submit()} destroyOnClose>
        <Form form={entityForm} layout="vertical" onFinish={handleEntitySave}>
          <Form.Item name="tenantCode" label="租户"><Input disabled /></Form.Item>
          <Form.Item name="domainCode" label="域"><Input disabled /></Form.Item>
          {entityType === 'dept' && <Form.Item name="orgCode" label="所属组织"><Input disabled /></Form.Item>}
          {entityType === 'group' && (
            <>
              <Form.Item name="orgCode" label="组织"><Input disabled /></Form.Item>
              <Form.Item name="deptCode" label="所属部门"><Input disabled /></Form.Item>
            </>
          )}
          <Form.Item name={entityType === 'org' ? 'orgCode' : entityType === 'dept' ? 'deptCode' : 'groupCode'}
            label="编码" rules={[{ required: true }]}>
            <Input /></Form.Item>
          <Form.Item name={entityType === 'org' ? 'orgName' : entityType === 'dept' ? 'deptName' : 'groupName'}
            label="名称" rules={[{ required: true }]}>
            <Input /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default OrgManage
