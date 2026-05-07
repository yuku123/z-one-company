import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, message, Popconfirm, Select, Tabs, Drawer } from 'antd'
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined, DeleteOutlined, HistoryOutlined, RollbackOutlined, DiffOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import { configApi } from '@/services/api'

const ConfigList = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [searchText, setSearchText] = useState('')
  const [selectedNamespace, setSelectedNamespace] = useState(undefined)
  const [selectedGroup, setSelectedGroup] = useState(undefined)
  const [namespaceList, setNamespaceList] = useState([])
  const [groupList, setGroupList] = useState([])
  const [historyVisible, setHistoryVisible] = useState(false)
  const [historyConfig, setHistoryConfig] = useState(null)
  const [historyData, setHistoryData] = useState([])
  const [historyLoading, setHistoryLoading] = useState(false)
  const [selectedVersion1, setSelectedVersion1] = useState(null)
  const [selectedVersion2, setSelectedVersion2] = useState(null)
  const [diffVisible, setDiffVisible] = useState(false)

  const fetchNamespaceList = async () => {
    try {
      const [clusters, nsList] = await Promise.all([
        configApi.clusterList().catch(() => []),
        configApi.namespaceList().catch(() => []),
      ])
      const clusterNames = (clusters || []).map(i => ({ label: i.name, value: i.name }))
      const configNames = (nsList || []).map(n => ({ label: n, value: n }))
      // 合并去重
      const seen = new Set()
      const merged = [...clusterNames, ...configNames].filter(i => { if (seen.has(i.value)) return false; seen.add(i.value); return true })
      setNamespaceList(merged.length > 0 ? merged : [{ label: '默认命名空间', value: 'DEFAULT_NAMESPACE' }])
      if (merged.length > 0 && !selectedNamespace) setSelectedNamespace(merged[0].value)
    } catch (e) { console.error('获取命名空间失败', e) }
  }

  const fetchGroupList = async () => {
    try {
      const list = await configApi.groupList()
      setGroupList((list || []).map(item => ({ label: item, value: item })))
    } catch (e) { console.error('获取Group失败', e) }
  }

  const fetchConfigList = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const result = await configApi.pageConfig({
        current: page,
        size: pageSize,
        search: searchText || undefined,
        nameSpace: selectedNamespace,
        group: selectedGroup,
      })
      if (result && result.records) {
        setData(result.records.map((item, i) => ({ key: item.id || i, ...item })))
        setPagination({ current: result.current || 1, pageSize: result.size || pageSize, total: result.total || 0 })
      }
    } catch (error) {
      message.error('获取配置列表失败')
    } finally { setLoading(false) }
  }

  const handleDelete = async (record) => {
    try {
      await configApi.deleteConfig({ nameSpace: record.namespace, group: record.group, dataId: record.dataId })
      message.success('删除成功')
      fetchConfigList(pagination.current, pagination.pageSize)
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleEdit = (record) => navigate('/config/edit', { state: { config: record } })
  const handleTableChange = (p) => fetchConfigList(p.current, p.pageSize)
  const handleSearch = () => fetchConfigList(1, pagination.pageSize)

  useEffect(() => { fetchConfigList(); fetchNamespaceList(); fetchGroupList() }, [location.key])

  // also re-fetch when namespace list changes (trigger fetch after tabs load)
  useEffect(() => { if (namespaceList.length > 0) fetchConfigList() }, [selectedNamespace])

  const columns = [
    { title: 'Data ID', dataIndex: 'dataId', key: 'dataId', ellipsis: true },
    { title: 'Group', dataIndex: 'group', key: 'group' },
    { title: '应用名', dataIndex: 'appName', key: 'appName' },
    { title: '类型', dataIndex: 'configType', key: 'configType',
      render: (type) => <Tag color="blue">{type || 'TEXT'}</Tag> },
    { title: '操作', key: 'action', width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除" description={`确定要删除 "${record.dataId}" 吗？`}
            onConfirm={() => handleDelete(record)} okText="确定" cancelText="取消">
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Card title="配置列表"
        extra={<Space><Button icon={<ReloadOutlined />} onClick={() => fetchConfigList(pagination.current, pagination.pageSize)} loading={loading}>刷新</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/config/edit')}>新建配置</Button></Space>}>
        <div style={{ marginBottom: 16 }}>
          <Tabs activeKey={selectedNamespace} onChange={(key) => { setSelectedNamespace(key); fetchConfigList(1, pagination.pageSize) }}
            items={namespaceList.map(ns => ({ key: ns.value, label: ns.label }))} />
          <Space size="middle">
            <Select placeholder="选择Group" style={{ width: 200 }} value={selectedGroup}
              onChange={setSelectedGroup} allowClear options={groupList} />
            <Input.Search placeholder="搜索 Data ID..." value={searchText}
              onChange={(e) => setSearchText(e.target.value)} onSearch={handleSearch}
              style={{ width: 300 }} prefix={<SearchOutlined />} allowClear />
            <Button type="primary" onClick={handleSearch}>搜索</Button>
          </Space>
        </div>
        <Table columns={columns} dataSource={data}
          pagination={{ ...pagination, showSizeChanger: true, showTotal: (t) => `共 ${t} 条` }}
          loading={loading} onChange={handleTableChange} />
      </Card>
    </div>
  )
}

export default ConfigList
