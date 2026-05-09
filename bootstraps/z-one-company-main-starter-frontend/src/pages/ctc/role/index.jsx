import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Tag, message, Modal, Form, Input, Tree, Popconfirm, Drawer } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, SaveOutlined, UnlockOutlined } from '@ant-design/icons'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, assignRolePermissions, getPermissionList } from '@/services/api'

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
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [searchText, setSearchText] = useState('')
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRole, setEditingRole] = useState(null)
  const [form] = Form.useForm()

  // 权限抽屉
  const [permDrawerOpen, setPermDrawerOpen] = useState(false)
  const [permLoading, setPermLoading] = useState(false)
  const [permList, setPermList] = useState([])
  const [permTreeData, setPermTreeData] = useState([])
  const [checkedPermIds, setCheckedPermIds] = useState([])
  const [savingPerm, setSavingPerm] = useState(false)
  const [currentRole, setCurrentRole] = useState(null)

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

  // 标准化权限字段
  const normalizePerm = (item) => ({
    ...item,
    permType: item.resourceType || item.permType,
    permName: item.permissionName || item.permName,
    permCode: item.permissionCode || item.permCode,
    sortOrder: item.sortOrder ?? item.sort,
  })

  // 加载全部权限树
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

  // 加载角色的已有权限
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

  // 打开权限抽屉
  const openPermDrawer = async (record) => {
    setCurrentRole(record)
    setPermDrawerOpen(true)
    await fetchRolePerms(record.id)
  }

  // 关闭抽屉
  const closePermDrawer = () => {
    setPermDrawerOpen(false)
    setCurrentRole(null)
    setCheckedPermIds([])
  }

  // 保存权限分配
  const handleSavePerms = async () => {
    if (!currentRole) return
    setSavingPerm(true)
    try {
      await assignRolePermissions(currentRole.id, checkedPermIds)
      message.success('权限分配成功')
      closePermDrawer()
    } catch (e) {
      message.error('保存失败')
    } finally {
      setSavingPerm(false)
    }
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

  // 分页/搜索
  const handleTableChange = (newPagination) => {
    fetchRoles(newPagination.current, newPagination.pageSize)
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '角色名称', dataIndex: 'roleName', key: 'roleName' },
    { title: '角色编码', dataIndex: 'roleCode', key: 'roleCode' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s) => <Tag color={s === 1 ? 'success' : 'default'}>{s === 1 ? '正常' : '停用'}</Tag>,
    },
    { title: '创建时间', dataIndex: 'gmtCreate', key: 'gmtCreate' },
    {
      title: '操作', key: 'action', width: 180,
      render: (_, record) => (
        <Space size={4}>
          <Button type="text" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Button type="text" size="small" icon={<UnlockOutlined />} onClick={() => openPermDrawer(record)}>绑定权限</Button>
          <Popconfirm title="确认删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="text" danger size="small" icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ]

  // 权限树节点渲染
  const renderPermNode = (node) => (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
      <span>{node.permName}</span>
      {TYPE_MAP[node.permType] && (
        <Tag color={TYPE_MAP[node.permType].color} style={{ margin: 0, fontSize: 10 }}>
          {TYPE_MAP[node.permType].label}
        </Tag>
      )}
    </span>
  )

  return (
    <div style={{ padding: 16 }}>
      <Card
        size="small"
        title="角色列表"
        extra={
          <Space size={4}>
            <Button size="small" icon={<ReloadOutlined />} onClick={() => fetchRoles(pagination.current, pagination.pageSize)}>刷新</Button>
            <Button type="primary" size="small" icon={<PlusOutlined />} onClick={handleAdd}>新增角色</Button>
          </Space>
        }
      >
        <div style={{ marginBottom: 12 }}>
          <Input.Search
            placeholder="搜索角色名称"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onSearch={() => fetchRoles(1, pagination.pageSize)}
            allowClear
            style={{ width: 240 }}
          />
        </div>
        <Table
          size="small"
          columns={columns}
          dataSource={roles}
          loading={loading}
          rowKey="id"
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showTotal: (t) => `共 ${t} 条`,
          }}
          onChange={handleTableChange}
        />
      </Card>

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
        </Form>
      </Modal>

      {/* 权限分配抽屉 */}
      <Drawer
        title={currentRole ? `绑定权限：${currentRole.roleName}` : '绑定权限'}
        open={permDrawerOpen}
        width={520}
        onClose={closePermDrawer}
        extra={
          <Button type="primary" icon={<SaveOutlined />} loading={savingPerm} onClick={handleSavePerms}>
            保存
          </Button>
        }
      >
        {currentRole && (
          <>
            <div style={{ color: '#666', fontSize: 12, marginBottom: 12 }}>
              勾选该角色拥有的权限（支持多选）
            </div>
            <Tree
              checkable
              treeData={permTreeData}
              checkedKeys={checkedPermIds}
              onCheck={setCheckedPermIds}
              titleRender={renderPermNode}
              blockNode
              height={600}
            />
          </>
        )}
      </Drawer>
    </div>
  )
}

export default RoleManagement
