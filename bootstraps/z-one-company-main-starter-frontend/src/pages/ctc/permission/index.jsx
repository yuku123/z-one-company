import { useState, useEffect } from 'react'
import { Card, Tree, Table, Button, Space, Tag, message, Modal, Form, Input, Select, Popconfirm, Typography } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons'
import { getPermissionList, createPermission, updatePermission, deletePermission } from '@/services/api'

const { Text } = Typography

// 类型映射
const TYPE_MAP = {
  MENU: { label: '菜单', color: 'blue' },
  BUTTON: { label: '按钮', color: 'green' },
  API: { label: 'API', color: 'orange' },
}

// 扁平列表转树形
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

const PermissionManagement = () => {
  const [loading, setLoading] = useState(false)
  const [rawList, setRawList] = useState([])
  const [treeData, setTreeData] = useState([])
  const [expandedKeys, setExpandedKeys] = useState([])
  const [selectedPerm, setSelectedPerm] = useState(null)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingPerm, setEditingPerm] = useState(null)
  const [form] = Form.useForm()
  const [saving, setSaving] = useState(false)

  // 标准化 API 响应字段名（后端 DTO 用 resourceType，前端代码用 permType/permName）
  const normalizePerm = (item) => ({
    ...item,
    permType: item.resourceType || item.permType,
    permName: item.permissionName || item.permName,
    permCode: item.permissionCode || item.permCode,
    sortOrder: item.sortOrder ?? item.sort,
  })

  // 加载权限列表
  const fetchPermissions = async () => {
    setLoading(true)
    try {
      const res = await getPermissionList()
      const list = (res?.data?.records || res || []).map(normalizePerm)
      setRawList(list)
      setTreeData(listToTree(list))
      if (list.length > 0 && !selectedPerm) {
        setExpandedKeys(list.filter(p => p.parentId === 0 || !p.parentId).map(p => p.id))
      }
    } catch (e) {
      message.error('获取权限列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPermissions()
  }, [])

  // 选中树节点
  const handleSelect = (selectedKeys) => {
    if (selectedKeys.length > 0) {
      const id = selectedKeys[0]
      const perm = rawList.find(p => p.id === id)
      setSelectedPerm(perm || null)
    } else {
      setSelectedPerm(null)
    }
  }

  // 新增
  const handleAdd = () => {
    setEditingPerm(null)
    form.resetFields()
    // 如果选了节点，默认其作为父节点
    if (selectedPerm) {
      form.setFieldsValue({ parentId: selectedPerm.id })
    }
    setModalVisible(true)
  }

  // 编辑
  const handleEdit = () => {
    if (!selectedPerm) return
    setEditingPerm(selectedPerm)
    form.setFieldsValue({
      permissionName: selectedPerm.permName,
      permissionCode: selectedPerm.permCode,
      permType: selectedPerm.permType,
      parentId: selectedPerm.parentId,
      path: selectedPerm.path,
      icon: selectedPerm.icon,
      sortOrder: selectedPerm.sortOrder,
      status: selectedPerm.status,
    })
    setModalVisible(true)
  }

  // 删除（软删）
  const handleDelete = async () => {
    if (!selectedPerm) return
    try {
      await deletePermission(selectedPerm.id)
      message.success('删除成功')
      setSelectedPerm(null)
      fetchPermissions()
    } catch (e) {
      message.error('删除失败')
    }
  }

  // 提交保存
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      setSaving(true)
      const payload = {
        permissionName: values.permissionName,
        permissionCode: values.permissionCode,
        resourceType: values.permType,
        parentId: values.parentId || 0,
        path: values.path || '',
        icon: values.icon || '',
        sort: values.sortOrder ?? 0,
        status: values.status ?? 1,
      }
      if (editingPerm) {
        await updatePermission(editingPerm.id, payload)
        message.success('更新成功')
      } else {
        await createPermission(payload)
        message.success('创建成功')
      }
      setModalVisible(false)
      fetchPermissions()
    } catch (e) {
      message.error(e.message || '操作失败')
    } finally {
      setSaving(false)
    }
  }

  // 渲染树节点
  const renderTreeNode = (node) => (
    <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
      <Text>{node.permName}</Text>
      {TYPE_MAP[node.permType] && (
        <Tag color={TYPE_MAP[node.permType].color} style={{ margin: 0, fontSize: 10 }}>
          {TYPE_MAP[node.permType].label}
        </Tag>
      )}
    </span>
  )

  // 选中节点的孩子（用于右侧子节点表格）
  const childrenOfSelected = selectedPerm
    ? rawList.filter(p => p.parentId === selectedPerm.id)
    : []

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      {/* 左侧：权限树 */}
      <div style={{ width: 320, flexShrink: 0 }}>
        <Card
          size="small"
          title="权限列表"
          extra={
            <Space size={4}>
              <Button type="text" size="small" icon={<ReloadOutlined />} onClick={fetchPermissions} />
              <Button type="text" size="small" icon={<PlusOutlined />} onClick={handleAdd} />
            </Space>
          }
          bodyStyle={{ padding: 8, overflow: 'auto', maxHeight: 'calc(100vh - 230px)' }}
        >
          <Tree
            showLine={{ showLeafIcon: false }}
            treeData={treeData}
            expandedKeys={expandedKeys}
            onExpand={(keys) => setExpandedKeys(keys)}
            selectedKeys={selectedPerm ? [selectedPerm.id] : []}
            onSelect={(keys) => handleSelect(keys)}
            titleRender={(node) => renderTreeNode(node)}
            blockNode
          />
        </Card>
      </div>

      {/* 右侧：选中权限详情 + 子节点列表 */}
      <div style={{ flex: 1, overflow: 'auto' }}>
        <Card
          size="small"
          title={selectedPerm ? `权限详情：${selectedPerm.permName}` : '请选择左侧权限节点'}
          extra={
            selectedPerm && (
              <Space>
                <Button size="small" icon={<EditOutlined />} onClick={handleEdit}>编辑</Button>
                <Popconfirm title="确认删除？" description="删除后可在子节点中恢复" onConfirm={handleDelete}>
                  <Button size="small" danger icon={<DeleteOutlined />}>删除</Button>
                </Popconfirm>
              </Space>
            )
          }
          bodyStyle={{ padding: selectedPerm ? 16 : 0 }}
        >
          {!selectedPerm && (
            <div style={{ textAlign: 'center', color: '#999', padding: '60px 0' }}>
              从左侧树选择一个权限节点查看详情
            </div>
          )}

          {selectedPerm && (
            <>
              {/* 详情字段 */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px 24px', marginBottom: 24 }}>
                <div>
                  <Text type="secondary">权限编码</Text>
                  <div><Text code>{selectedPerm.permCode}</Text></div>
                </div>
                <div>
                  <Text type="secondary">权限类型</Text>
                  <div>
                    {TYPE_MAP[selectedPerm.permType] && (
                      <Tag color={TYPE_MAP[selectedPerm.permType].color}>{TYPE_MAP[selectedPerm.permType].label}</Tag>
                    )}
                  </div>
                </div>
                <div>
                  <Text type="secondary">路径</Text>
                  <div><Text>{selectedPerm.path || '-'}</Text></div>
                </div>
                <div>
                  <Text type="secondary">图标</Text>
                  <div><Text>{selectedPerm.icon || '-'}</Text></div>
                </div>
                <div>
                  <Text type="secondary">排序</Text>
                  <div><Text>{selectedPerm.sortOrder ?? 0}</Text></div>
                </div>
                <div>
                  <Text type="secondary">状态</Text>
                  <div>
                    <Tag color={selectedPerm.status === 1 ? 'success' : 'default'}>
                      {selectedPerm.status === 1 ? '正常' : '停用'}
                    </Tag>
                  </div>
                </div>
              </div>

              {/* 子节点表格 */}
              <Text strong style={{ display: 'block', marginBottom: 8 }}>
                子权限 ({childrenOfSelected.length})
              </Text>
              {childrenOfSelected.length > 0 ? (
                <Table
                  size="small"
                  dataSource={childrenOfSelected}
                  rowKey="id"
                  pagination={false}
                  columns={[
                    { title: '权限名称', dataIndex: 'permName', key: 'permName' },
                    { title: '编码', dataIndex: 'permCode', key: 'permCode' },
                    {
                      title: '类型', dataIndex: 'permType', key: 'permType',
                      render: (t) => TYPE_MAP[t] ? <Tag color={TYPE_MAP[t].color}>{TYPE_MAP[t].label}</Tag> : t,
                    },
                    { title: '路径', dataIndex: 'path', key: 'path', ellipsis: true },
                    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 60 },
                  ]}
                />
              ) : (
                <div style={{ textAlign: 'center', color: '#bbb', padding: '16px 0' }}>
                  暂无子权限
                </div>
              )}
            </>
          )}
        </Card>
      </div>

      {/* 新增/编辑 Modal */}
      <Modal
        title={editingPerm ? '编辑权限' : '新增权限'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        confirmLoading={saving}
        width={520}
        destroyOnClose
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item
            name="permissionName"
            label="权限名称"
            rules={[{ required: true, message: '请输入权限名称' }]}
          >
            <Input placeholder="如：用户管理" />
          </Form.Item>

          <Form.Item
            name="permissionCode"
            label="权限编码"
            rules={[{ required: true, message: '请输入权限编码' }]}
          >
            <Input placeholder="如：system:user" disabled={!!editingPerm} />
          </Form.Item>

          <Form.Item
            name="permType"
            label="权限类型"
            rules={[{ required: true, message: '请选择权限类型' }]}
          >
            <Select placeholder="选择类型">
              <Select.Option value="MENU">菜单</Select.Option>
              <Select.Option value="BUTTON">按钮</Select.Option>
              <Select.Option value="API">API</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="parentId" label="父级权限">
            <Tree
              treeData={treeData}
              placeholder="不选则为顶级"
              allowClear
              showSearch
              style={{ width: '100%' }}
              treeNodeFilterProp="title"
            />
          </Form.Item>

          <Form.Item name="path" label="权限路径">
            <Input placeholder="如：/system/user" />
          </Form.Item>

          <Form.Item name="icon" label="图标">
            <Input placeholder="如：UserOutlined" />
          </Form.Item>

          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="sortOrder" label="排序" style={{ flex: 1 }}>
              <Input type="number" placeholder="数字越小越靠前" />
            </Form.Item>
            <Form.Item name="status" label="状态" initialValue={1} style={{ flex: 1 }}>
              <Select>
                <Select.Option value={1}>正常</Select.Option>
                <Select.Option value={0}>停用</Select.Option>
              </Select>
            </Form.Item>
          </div>
        </Form>
      </Modal>
    </div>
  )
}

export default PermissionManagement
