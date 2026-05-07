import { useState, useEffect } from 'react'
import { Card, Tree, Button, Space, Tag, Modal, Form, Input, Select, message, Popconfirm, Empty, Descriptions } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined, AppstoreOutlined, MenuOutlined } from '@ant-design/icons'
import { appApi, getTenantList } from '@/services/api'

const AppPage = () => {
  const [tenantOptions, setTenantOptions] = useState([])
  const [selectedTenant, setSelectedTenant] = useState(null)
  const [apps, setApps] = useState([])
  const [selectedApp, setSelectedApp] = useState(null)
  const [menus, setMenus] = useState([])
  const [selectedMenu, setSelectedMenu] = useState(null)
  const [appModal, setAppModal] = useState(false)
  const [menuModal, setMenuModal] = useState(false)
  const [editingApp, setEditingApp] = useState(null)
  const [editingMenu, setEditingMenu] = useState(null)
  const [appForm] = Form.useForm()
  const [menuForm] = Form.useForm()

  useEffect(() => {
    (async () => {
      const tenants = await getTenantList()
      setTenantOptions((tenants || []).map(t => ({ label: `${t.tenantName} (${t.tenantCode})`, value: t.tenantCode })))
      const saved = localStorage.getItem('z_tenant')
      if (saved && (tenants || []).some(t => t.tenantCode === saved)) {
        setSelectedTenant(saved)
      } else if (tenants && tenants.length > 0) {
        setSelectedTenant(tenants[0].tenantCode)
      }
    })()
  }, [])

  useEffect(() => { if (selectedTenant) loadApps(selectedTenant) }, [selectedTenant])

  const loadApps = async (tenant) => {
    if (!tenant) return
    try {
      const list = await appApi.list({ tenantCode: tenant }).catch(() => [])
      setApps(list || [])
      setSelectedApp(null); setMenus([])
    } catch (e) { message.error('加载应用失败') }
  }

  const loadMenus = async (appCode) => {
    try {
      const list = await appApi.menuList(appCode)
      setMenus(list || [])
    } catch (e) { message.error('加载菜单失败') }
  }

  // 构建菜单树
  const buildMenuTree = (items, parentCode = null) =>
    items.filter(m => (m.parentCode || null) === parentCode).map(m => ({
      key: `menu:${m.id}`,
      title: (
        <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
          {m.menuName}
          <Tag color={m.menuType === 'button' ? 'blue' : m.menuType === 'feature' ? 'purple' : 'green'} style={{ fontSize: 10 }}>
            {m.menuType}
          </Tag>
          {m.permissionCode && <Tag style={{ fontSize: 10 }}>{m.permissionCode}</Tag>}
          <Popconfirm title="确认删除？" onConfirm={() => handleDeleteMenu(m)}>
            <Button type="text" size="small" danger icon={<DeleteOutlined style={{ fontSize: 11 }} />} onClick={e => e.stopPropagation()} />
          </Popconfirm>
        </span>
      ),
      data: m,
      isLeaf: !items.some(c => c.parentCode === m.menuCode),
      children: buildMenuTree(items, m.menuCode),
    }))

  const handleDeleteApp = async (app) => {
    try { await appApi.delete(app.id); message.success('删除成功'); loadApps(selectedTenant) }
    catch (e) { message.error('删除失败') }
  }

  const handleDeleteMenu = async (menu) => {
    try { await appApi.deleteMenu(menu.id); message.success('删除成功'); loadMenus(selectedApp.appCode) }
    catch (e) { message.error('删除失败') }
  }

  return (
    <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 180px)' }}>
      {/* 左侧：应用列表 */}
      <Card size="small" title="应用列表" style={{ width: 260, flexShrink: 0, overflow: 'auto' }}
        extra={
          <Space>
            <Button size="small" icon={<PlusOutlined />} onClick={() => { setEditingApp(null); appForm.resetFields(); setAppModal(true) }} />
            <Button size="small" icon={<ReloadOutlined />} onClick={() => loadApps(selectedTenant)} />
          </Space>
        }>
        <Select placeholder="选择租户" style={{ width: '100%', marginBottom: 8 }}
          value={selectedTenant} onChange={(v) => { setSelectedTenant(v); loadApps(v) }} allowClear options={tenantOptions} />
        {selectedTenant && apps.map(app => (
          <div key={app.id}
            onClick={() => { setSelectedApp(app); loadMenus(app.appCode) }}
            style={{
              padding: '8px 12px', cursor: 'pointer', borderRadius: 4, marginBottom: 4,
              background: selectedApp?.id === app.id ? '#e6f4ff' : undefined,
              display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            }}>
            <Space><AppstoreOutlined /><span>{app.appName} ({app.appCode})</span></Space>
            <Space>
              <Button type="text" size="small" icon={<EditOutlined style={{ fontSize: 11 }} />}
                onClick={(e) => { e.stopPropagation(); setEditingApp(app); appForm.setFieldsValue(app); setAppModal(true) }} />
              <Popconfirm title="确认删除？" onConfirm={() => handleDeleteApp(app)}>
                <Button type="text" size="small" danger icon={<DeleteOutlined style={{ fontSize: 11 }} />} onClick={e => e.stopPropagation()} />
              </Popconfirm>
            </Space>
          </div>
        ))}
      </Card>

      {/* 中间：菜单树 */}
      <Card size="small" title={selectedApp ? `${selectedApp.appName} - 菜单树` : '菜单树'} style={{ width: 340, overflow: 'auto' }}
        extra={
          selectedApp ? (
            <Button size="small" icon={<PlusOutlined />} onClick={() => { setEditingMenu(null); menuForm.resetFields(); menuForm.setFieldsValue({ appCode: selectedApp.appCode }); setMenuModal(true) }} />
          ) : null
        }>
        {selectedApp ? (
          menus.length > 0
            ? <Tree treeData={buildMenuTree(menus)} showLine showIcon={false} defaultExpandAll
                onSelect={(keys, info) => {
                  if (info.node.data) setSelectedMenu(info.node.data)
                }} />
            : <Empty description="暂无菜单，请新增" />
        ) : <Empty description="请选择应用" />}
      </Card>

      {/* 右侧：菜单详情 */}
      <Card size="small" style={{ flex: 1, overflow: 'auto' }}>
        {!selectedMenu ? <Empty description="请选择菜单项" /> : (
          <Descriptions title="菜单详情" size="small" bordered column={2}>
            <Descriptions.Item label="名称">{selectedMenu.menuName}</Descriptions.Item>
            <Descriptions.Item label="编码">{selectedMenu.menuCode}</Descriptions.Item>
            <Descriptions.Item label="类型"><Tag>{selectedMenu.menuType}</Tag></Descriptions.Item>
            <Descriptions.Item label="权限标识">{selectedMenu.permissionCode || '-'}</Descriptions.Item>
            <Descriptions.Item label="路径">{selectedMenu.path || '-'}</Descriptions.Item>
            <Descriptions.Item label="排序">{selectedMenu.sortOrder || 0}</Descriptions.Item>
            <Descriptions.Item label="描述">{selectedMenu.description || '-'}</Descriptions.Item>
          </Descriptions>
        )}
        {selectedMenu && (
          <Button type="primary" size="small" icon={<EditOutlined />} style={{ marginTop: 16 }}
            onClick={() => { setEditingMenu(selectedMenu); menuForm.setFieldsValue(selectedMenu); setMenuModal(true) }}>
            编辑
          </Button>
        )}
      </Card>

      {/* 应用 Modal */}
      <Modal title={editingApp ? '编辑应用' : '新增应用'} open={appModal}
        onCancel={() => setAppModal(false)} onOk={() => appForm.submit()}>
        <Form form={appForm} layout="vertical" onFinish={async (v) => {
          try { if (editingApp) { await appApi.update({ ...v, id: editingApp.id }) } else { await appApi.create({ ...v, tenantCode: selectedTenant }) }; message.success('操作成功'); setAppModal(false); loadApps(selectedTenant) } catch (e) { message.error('操作失败') }
        }}>
          <Form.Item name="appCode" label="应用编码" rules={[{ required: true }]}><Input disabled={!!editingApp} /></Form.Item>
          <Form.Item name="appName" label="应用名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="icon" label="图标"><Input placeholder="如：SettingOutlined" /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>

      {/* 菜单 Modal */}
      <Modal title={editingMenu ? '编辑菜单' : '新增菜单'} open={menuModal}
        onCancel={() => setMenuModal(false)} onOk={() => menuForm.submit()}>
        <Form form={menuForm} layout="vertical" onFinish={async (v) => {
          try {
            if (editingMenu) { await appApi.updateMenu({ ...v, id: editingMenu.id }) } else { await appApi.createMenu(v) }
            message.success('操作成功'); setMenuModal(false); loadMenus(selectedApp.appCode)
          } catch (e) { message.error('操作失败') }
        }}>
          <Form.Item name="appCode" label="所属应用"><Input disabled /></Form.Item>
          <Form.Item name="menuCode" label="菜单编码" rules={[{ required: true }]}><Input disabled={!!editingMenu} /></Form.Item>
          <Form.Item name="menuName" label="菜单名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="parentCode" label="父菜单编码">
            <Select allowClear placeholder="留空为顶级">
              {(menus || []).filter(m => editingMenu ? m.menuCode !== editingMenu.menuCode : true).map(m => (
                <Select.Option key={m.menuCode} value={m.menuCode}>{m.menuName} ({m.menuCode})</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="menuType" label="类型" initialValue="menu">
            <Select><Select.Option value="menu">菜单</Select.Option><Select.Option value="button">按钮</Select.Option><Select.Option value="feature">功能</Select.Option></Select>
          </Form.Item>
          <Form.Item name="permissionCode" label="权限标识"><Input placeholder="如：user:create" /></Form.Item>
          <Form.Item name="path" label="路由路径"><Input placeholder="如：/user/list" /></Form.Item>
          <Form.Item name="sortOrder" label="排序" initialValue={0}><Input /></Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default AppPage
