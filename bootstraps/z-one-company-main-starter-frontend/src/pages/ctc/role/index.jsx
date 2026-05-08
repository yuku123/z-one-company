import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Tag, message, Modal, Form, Input, Tree, Popconfirm, Typography } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SaveOutlined } from '@ant-design/icons'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, assignRolePermissions, getPermissionList } from '@/services/api'

const { Text } = Typography

const TYPE_MAP = {
  MENU: { label: '菜单', color: 'blue' },
  BUTTON: { label: '按钮', color: 'green' },
  API: { label: 'API', color: 'orange' },
}

const listToTree = (list) => {
  const map = {}
  const roots = []
  list.forEach(item => {
    map[item.id] = { ...item, key: item.id, title: item.permName, children: [] }
  })
  list.forEach(item => {
    if (item.parentId && map[item.parentId]) {
      map[item.parentId].children.push(map[item.id])
    } else {
      roots.push(map[item.id])
    }
  })
  return roots
}

const RoleManagement = () => {
  const [loading, setLoading] = useState(false)
  const [roles, setRoles] = useState([])
  const [selectedRole, setSelectedRole] = useState(null)
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [searchText, setSearchText] = useState('')
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRole, setEditingRole] = useState(null)
  const [form] = Form.useForm()

  // 权限分配
  const [permLoading, setPermLoading] = useState(false)
  const [permList, setPermList] = useState([])
  const [permTreeData, setPermTreeData] = useState([])
  const [checkedPermIds, setCheckedPermIds] = useState([])
  const [savingPerm, setSavingPerm] = useState(false)

  // 加载角色列表
  const fetchRoles = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res = await getRoleList({ current: page, size: pageSize, roleName: searchText || undefined })
      const records = res?.records || []
      setRoles(records)
      setPagination({ current: page, pageSize, total: res?.total || 0 })
    } catch (e) {
      message.error('获取角色列表失败')
    } finally {
      setLoading(false)
    }
  }

  // 标准化 API 响应字段名
  const normalizePerm = (item) => ({
    ...item,
    permType: item.resourceType || item.permType,
    permName: item.permissionName || item.permName,
    permCode: item.permissionCode || item.permCode,
    sortOrder: item.sortOrder ?? item.sort,
  })

  // 加载全部权限（树形）
  const fetchPermTree = async () => {
    setPermLoading(true)
    try {
      const res = await getPermissionList()
      const list = (res?.data?.records || res || []).map(normalizePerm)
      setPermList(list)
      setPermTreeData(listToTree(list))
    } catch (e) {
      message.error('获取权限列表失败')
    } finally {
      setPermLoading(false)
    }
  }

  // 加载选中角色的已有权限
  const fetchRolePerms = async (roleId) => {
    try {
      const res = await getRolePermissions(roleId)
      const perms = res || []
      setCheckedPermIds(perms.map(p => p.id))
    } catch (e) {
      setCheckedPermIds([])
    }
  }

  useEffect(() => {
    fetchRoles()
    fetchPermTree()
  }, [])

  // 选中角色
  const handleRoleSelect = (record) => {
    setSelectedRole(record)
    fetchRolePerms(record.id)
  }

  // 分页/搜索
  const handleTableChange = (newPagination, filters, sorter) => {
    fetchRoles(newPagination.current, newPagination.pageSize)
  }

  const handleSearch = () => {
    fetchRoles(1, pagination.pageSize)
  }

  // 角色 CRUD
  const handleAdd = () => {
    setEditingRole(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingRole(record)
    form.setFieldsValue({
      roleName: record.roleName,
      roleCode: record.roleCode,
      description: record.description,
      status: record.status,
    })
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await deleteRole(id)
      message.success('删除成功')
      if (selectedRole?.id === id) setSelectedRole(null)
      fetchRoles(pagination.current, pagination.pageSize)
    } catch (e) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingRole) {
        await updateRole(editingRole.id, values)
        message.success('更新成功')
      } else {
        await createRole(values)
        message.success('创建成功')
      }
      setModalVisible(false)
      fetchRoles(pagination.current, pagination.pageSize)
    } catch (e) {
      message.error(e.message || '操作失败')
    }
  }

  // 权限分配
  const handlePermChange = (checked) => {
    setCheckedPermIds(checked)
  }

  const handleSavePerms = async () => {
    if (!selectedRole) return
    setSavingPerm(true)
    try {
      await assignRolePermissions(selectedRole.id, checkedPermIds)
      message.success('权限分配成功')
    } catch (e) {
      message.error('保存失败')
    } finally {
      setSavingPerm(false)
    }
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    {
      title: '角色名称', dataIndex: 'roleName', key: 'roleName',
      render: (text, record) => (
        <a onClick={() => handleRoleSelect(record)} style={{ fontWeight: selectedRole?.id === record.id ? 600 : 400 }}>
          {text}
        </a>
      ),
    },
    { title: '角色编码', dataIndex: 'roleCode', key: 'roleCode' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s) => <Tag color={s === 1 ? 'success' : 'default'}>{s === 1 ? '正常' : '停用'}</Tag>,
    },
    { title: '创建时间', dataIndex: 'gmtCreate', key: 'gmtCreate' },
    {
      title: '操作', key: 'action', width: 120,
      render: (_, record) => (
        <Space size={4}>
          <Button type="text" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)} />
          <Popconfirm title="确认删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="text" size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ]

  // 权限树节点渲染
  const renderPermNode = (node) => (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
      <Text>{node.permName}</Text>
      {TYPE_MAP[node.permType] && (
        <Tag color={TYPE_MAP[node.permType].color} style={{ margin: 0, fontSize: 10 }}>
          {TYPE_MAP[node.permType].label}
        </Tag>
      )}
    </span>
  )

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      {/* 左侧：角色列表 */}
      <div style={{ width: 500, flexShrink: 0 }}>
        <Card
          size="small"
          title="角色列表"
          extra={
            <Space size={4}>
              <Button type="text" size="small" icon={<ReloadOutlined />} onClick={() => fetchRoles(pagination.current, pagination.pageSize)} />
              <Button type="text" size="small" icon={<PlusOutlined />} onClick={handleAdd} />
            </Space>
          }
          bodyStyle={{ padding: 0 }}
        >
          <div style={{ padding: '0 12px 8px' }}>
            <Input.Search
              placeholder="搜索角色名称"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onSearch={handleSearch}
              allowClear
            />
          </div>
          <Table
            size="small"
            columns={columns}
            dataSource={roles}
            loading={loading}
            rowKey="id"
            rowClassName={(record) => selectedRole?.id === record.id ? 'ant-table-row-selected' : ''}
            pagination={{
              ...pagination,
              showSizeChanger: true,
              showTotal: (t) => `共 ${t} 条`,
            }}
            onChange={handleTableChange}
            onRow={(record) => ({
              onClick: () => handleRoleSelect(record),
              style: { cursor: 'pointer' },
            })}
          />
        </Card>
      </div>

      {/* 右侧：角色权限分配 */}
      <div style={{ flex: 1, overflow: 'auto' }}>
        <Card
          size="small"
          title={selectedRole ? `权限分配：${selectedRole.roleName}` : '请选择左侧角色'}
          extra={
            selectedRole && (
              <Button
                type="primary"
                size="small"
                icon={<SaveOutlined />}
                loading={savingPerm}
                onClick={handleSavePerms}
              >
                保存分配
              </Button>
            )
          }
          bodyStyle={{ padding: selectedRole ? 12 : 24 }}
        >
          {!selectedRole && (
            <div style={{ textAlign: 'center', color: '#999', padding: '60px 0' }}>
              从左侧选择一个角色进行权限分配
            </div>
          )}
          {selectedRole && (
            <div style={{ color: '#666', fontSize: 12, marginBottom: 12 }}>
              勾选该角色拥有的权限（支持多选）
            </div>
          )}
          {selectedRole && (
            <Tree
              checkable
              treeData={permTreeData}
              checkedKeys={checkedPermIds}
              onCheck={handlePermChange}
              titleRender={renderPermNode}
              blockNode
              loadData={null}
              height={500}
            />
          )}
        </Card>
      </div>

      {/* 新增/编辑角色 Modal */}
      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={480}
        destroyOnClose
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item
            name="roleName"
            label="角色名称"
            rules={[{ required: true, message: '请输入角色名称' }]}
          >
            <Input placeholder="如：超级管理员" />
          </Form.Item>
          <Form.Item
            name="roleCode"
            label="角色编码"
            rules={[{ required: true, message: '请输入角色编码' }]}
            extra="唯一标识，建议用英文下划线命名"
          >
            <Input placeholder="如：super_admin" disabled={!!editingRole} />
          </Form.Item>
          <Form.Item name="description" label="角色描述">
            <Input.TextArea placeholder="描述角色职责" rows={2} />
          </Form.Item>
          {!editingRole && (
            <Form.Item name="status" label="状态" initialValue={1}>
              <Input type="number" hidden />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  )
}

export default RoleManagement
