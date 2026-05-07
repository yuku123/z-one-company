import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Tag, message, Popconfirm, Modal, Form, Input, Select, Drawer, Tree } from 'antd'
import { PlusOutlined, ReloadOutlined, ApartmentOutlined } from '@ant-design/icons'
import { getUserList, createUser, updateUser, deleteUser, userOrgRelApi } from '@/services/api'
import { getTenantList, getDomainByTenantCode } from '@/services/api'
import { getOrgByDomainCode, getDeptByOrgCode, getGroupByDeptCode } from '@/services/api'

const UserManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [form] = Form.useForm()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingUser, setEditingUser] = useState(null)

  const [filters, setFilters] = useState({ userName: '', realName: '', status: undefined })

  // 组织关联抽屉
  const [orgDrawerOpen, setOrgDrawerOpen] = useState(false)
  const [orgDrawerUser, setOrgDrawerUser] = useState(null)
  const [orgTenantOptions, setOrgTenantOptions] = useState([])
  const [orgDomainOptions, setOrgDomainOptions] = useState([])
  const [orgSelTenant, setOrgSelTenant] = useState(null)
  const [orgSelDomain, setOrgSelDomain] = useState(null)
  const [orgTree, setOrgTree] = useState([])
  const [checkedKeys, setCheckedKeys] = useState([])
  const [nodeMap, setNodeMap] = useState({}) // key → node data

  // ===== 用户列表 =====
  const fetchUserList = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res = await getUserList({
        pageNum: page, pageSize,
        userName: filters.userName || undefined,
        realName: filters.realName || undefined,
        status: filters.status,
      })
      setData(res?.records || [])
      setPagination({ current: res?.current || 1, pageSize, total: res?.total || 0 })
    } catch (e) { message.error('获取用户列表失败') } finally { setLoading(false) }
  }

  useEffect(() => { fetchUserList() }, [])

  const handleSearch = () => fetchUserList(1, pagination.pageSize)
  const handleReset = () => { setFilters({ userName: '', realName: '', status: undefined }); fetchUserList(1, pagination.pageSize) }
  const handleTableChange = (p) => fetchUserList(p.current, p.pageSize)

  const handleAdd = () => { setEditingUser(null); form.resetFields(); setModalVisible(true) }
  const handleEdit = (r) => { setEditingUser(r); form.setFieldsValue(r); setModalVisible(true) }
  const handleDelete = async (id) => { try { await deleteUser(id); message.success('删除成功'); fetchUserList(pagination.current, pagination.pageSize) } catch (e) { message.error('删除失败') } }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingUser) { await updateUser(editingUser.id, values); message.success('更新成功') }
      else { await createUser(values); message.success('创建成功') }
      setModalVisible(false); fetchUserList(pagination.current, pagination.pageSize)
    } catch (e) { message.error(e.response?.data?.message || '操作失败') }
  }

  // ===== 组织关联抽屉 =====
  const openOrgDrawer = async (record) => {
    setOrgDrawerUser(record)
    setCheckedKeys(record.deptCode ? [] : [])
    setOrgSelTenant(null)
    setOrgSelDomain(null)
    setOrgTree([])
    const tenants = await getTenantList()
    setOrgTenantOptions((tenants || []).map(t => ({ label: `${t.tenantName} (${t.tenantCode})`, value: t.tenantCode })))
    setOrgDrawerOpen(true)
  }

  const onOrgTenantChange = async (code) => {
    setOrgSelTenant(code)
    setOrgSelDomain(null)
    setOrgTree([])
    if (!code) return
    const domains = await getDomainByTenantCode(code)
    setOrgDomainOptions((domains || []).map(d => ({ label: `${d.domainName} (${d.domainCode})`, value: d.domainCode })))
  }

  const onOrgDomainChange = async (code) => {
    setOrgSelDomain(code)
    if (!code) { setOrgTree([]); setNodeMap({}); return }
    try {
      const orgs = await getOrgByDomainCode(code)
      if (!orgs || orgs.length === 0) { setOrgTree([]); setNodeMap({}); return }
      const tree = []; const map = {}
      for (const o of (orgs || [])) {
        const orgKey = `org:${o.orgCode}`
        map[orgKey] = { type: 'org', code: o.orgCode, data: o }
        const orgNode = {
          key: orgKey,
          title: <span><Tag color="#1677ff">组织</Tag>{o.orgName || o.orgCode}</span>,
          children: [],
        }
        const depts = await getDeptByOrgCode(o.orgCode)
        for (const d of (depts || [])) {
          const deptKey = `dept:${d.deptCode}`
          map[deptKey] = { type: 'dept', code: d.deptCode, parentOrg: o.orgCode, data: d }
          const deptNode = {
            key: deptKey,
            title: <span><Tag color="#52c41a">部门</Tag>{d.deptName || d.deptCode}</span>,
            children: [],
          }
          const groups = await getGroupByDeptCode(d.deptCode)
          for (const g of (groups || [])) {
            const groupKey = `group:${g.groupCode}`
            map[groupKey] = { type: 'group', code: g.groupCode, parentOrg: o.orgCode, parentDept: d.deptCode, data: g }
            deptNode.children.push({
              key: groupKey,
              title: <span><Tag color="#fa8c16">组</Tag>{g.groupName || g.groupCode}</span>,
              isLeaf: true,
            })
          }
          orgNode.children.push(deptNode)
        }
        tree.push(orgNode)
      }
      setOrgTree(tree)
      setNodeMap(map)
    } catch (e) {
      console.error('加载组织树失败', e)
      message.error('加载组织树失败')
      setOrgTree([])
    }
  }

  const handleOrgConfirm = async () => {
    if (checkedKeys.length === 0) { message.warning('请至少选择一个节点'); return }
    try {
      await userOrgRelApi.clearUser(orgDrawerUser.id)
      for (const key of checkedKeys) {
        const info = nodeMap[key]
        if (!info) continue
        const bindData = {
          userId: orgDrawerUser.id,
          tenantCode: orgSelTenant,
          domainCode: orgSelDomain,
          deptCode: info.type === 'dept' ? info.code : info.parentDept || null,
          groupCode: info.type === 'group' ? info.code : null,
        }
        await userOrgRelApi.bind(bindData)
      }
      message.success('关联成功')
      setOrgDrawerOpen(false)
      fetchUserList(pagination.current, pagination.pageSize)
    } catch (e) { message.error('关联失败') }
  }

  const columns = [
    { title: '用户名', dataIndex: 'userName', key: 'userName' },
    { title: '昵称', dataIndex: 'nickName', key: 'nickName' },
    { title: '手机号', dataIndex: 'phone', key: 'phone' },
    { title: '邮箱', dataIndex: 'email', key: 'email' },
    { title: '关联部门', dataIndex: 'deptCode', key: 'deptCode',
      render: (v) => v ? <Tag color="blue">{v}</Tag> : <Tag>未关联</Tag> },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (s) => <Tag color={s === 1 ? 'success' : 'default'}>{s === 1 ? '正常' : '停用'}</Tag> },
    { title: '操作', key: 'action', width: 220,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => handleEdit(record)}>编辑</Button>
          <Button type="link" size="small" icon={<ApartmentOutlined />}
            onClick={() => openOrgDrawer(record)}>关联组织</Button>
          <Popconfirm title="确认删除" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" danger size="small">删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <Card title="用户管理"
      extra={<Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增用户</Button>}>
      <div style={{ marginBottom: 16, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        <Input placeholder="用户名" value={filters.userName}
          onChange={e => setFilters(f => ({ ...f, userName: e.target.value }))}
          onPressEnter={handleSearch} style={{ width: 140 }} allowClear />
        <Input placeholder="昵称" value={filters.realName}
          onChange={e => setFilters(f => ({ ...f, realName: e.target.value }))}
          onPressEnter={handleSearch} style={{ width: 140 }} allowClear />
        <Select placeholder="状态" value={filters.status}
          onChange={v => setFilters(f => ({ ...f, status: v }))} style={{ width: 100 }} allowClear>
          <Select.Option value={1}>正常</Select.Option>
          <Select.Option value={0}>停用</Select.Option>
        </Select>
        <Button type="primary" onClick={handleSearch}>查询</Button>
        <Button onClick={handleReset}>重置</Button>
        <Button icon={<ReloadOutlined />} onClick={() => fetchUserList(pagination.current, pagination.pageSize)}>刷新</Button>
      </div>

      <Table columns={columns} dataSource={data} loading={loading}
        pagination={{ ...pagination, showSizeChanger: true, showTotal: t => `共 ${t} 条` }}
        onChange={handleTableChange} rowKey="id" />

      {/* 新建/编辑用户 Modal */}
      <Modal title={editingUser ? '编辑用户' : '新增用户'}
        open={modalVisible} onOk={handleSubmit} onCancel={() => setModalVisible(false)} width={600}>
        <Form form={form} layout="vertical">
          <Form.Item name="userName" label="用户名" rules={[{ required: true }]}>
            <Input disabled={!!editingUser} /></Form.Item>
          <Form.Item name="nickName" label="昵称"><Input /></Form.Item>
          <Form.Item name="phone" label="手机号"><Input /></Form.Item>
          <Form.Item name="email" label="邮箱"><Input /></Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: !editingUser }]}>
            <Input.Password placeholder={editingUser ? '留空不修改' : ''} /></Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select><Select.Option value={1}>正常</Select.Option><Select.Option value={0}>停用</Select.Option></Select></Form.Item>
        </Form>
      </Modal>

      {/* 关联组织抽屉 */}
      <Drawer title={`关联组织 - ${orgDrawerUser?.userName || ''}`}
        open={orgDrawerOpen} onClose={() => setOrgDrawerOpen(false)} width={520}
        extra={<Button type="primary" onClick={handleOrgConfirm}>确认关联</Button>}>
        <div style={{ marginBottom: 12 }}>
          <Select placeholder="选择租户" style={{ width: '100%', marginBottom: 8 }}
            value={orgSelTenant} onChange={onOrgTenantChange} allowClear options={orgTenantOptions} />
          <Select placeholder="选择域" style={{ width: '100%' }}
            value={orgSelDomain} onChange={onOrgDomainChange} allowClear options={orgDomainOptions}
            disabled={!orgSelTenant} />
        </div>
        {orgSelDomain && orgTree.length > 0 ? (
          <Tree checkable treeData={orgTree} showLine showIcon={false}
            checkedKeys={checkedKeys}
            onCheck={(checked) => setCheckedKeys(checked)} />
        ) : (
          <div style={{ color: '#999', textAlign: 'center', marginTop: 40 }}>
            {orgSelDomain ? '加载中...' : '请先选择租户和域'}
          </div>
        )}
        {checkedKeys.length > 0 && (
          <div style={{ marginTop: 12, padding: '8px 12px', background: '#f6ffed', borderRadius: 4 }}>
            已选 {checkedKeys.length} 个节点：
            {checkedKeys.map(k => <Tag key={k} color="green">{k.replace(/^(org|dept|group):/, '')}</Tag>)}
          </div>
        )}
      </Drawer>
    </Card>
  )
}

export default UserManagement
