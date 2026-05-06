import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, message, Select } from 'antd'
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons'
import { configApi } from '@/services/api'

const ConfigHistory = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 })
  const [searchText, setSearchText] = useState('')
  const [selectedNamespace, setSelectedNamespace] = useState(undefined)
  const [selectedGroup, setSelectedGroup] = useState(undefined)
  const [namespaceList, setNamespaceList] = useState([])
  const [groupList, setGroupList] = useState([])

  const fetchNamespaceList = async () => {
    try {
      const res = await configApi.clusterList()
      if (res.success) setNamespaceList((res.data || []).map(i => ({ label: i.name, value: i.name })))
    } catch (e) { console.error(e) }
  }

  const fetchGroupList = async () => {
    try {
      const res = await configApi.groupList()
      if (res.success) setGroupList((res.data || []).map(i => ({ label: i, value: i })))
    } catch (e) { console.error(e) }
  }

  const fetchHistoryList = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const res = await configApi.historyPage({
        pageNum: page, pageSize,
        search: searchText || undefined,
        namespace: selectedNamespace,
        group: selectedGroup,
      })
      if (res.success && res.data) {
        setData((res.data.records || []).map((item, idx) => ({ key: item.id || idx, ...item })))
        setPagination({ current: res.data.current || 1, pageSize, total: res.data.total || 0 })
      }
    } catch (e) { message.error('获取变更历史失败') } finally { setLoading(false) }
  }

  const handleTableChange = (p) => fetchHistoryList(p.current, p.pageSize)
  const handleSearch = () => fetchHistoryList(1, pagination.pageSize)

  useEffect(() => { fetchHistoryList(); fetchNamespaceList(); fetchGroupList() }, [])

  const columns = [
    { title: 'Data ID', dataIndex: 'dataId', key: 'dataId', ellipsis: true },
    { title: 'Group', dataIndex: 'group', key: 'group' },
    { title: '应用名', dataIndex: 'appName', key: 'appName' },
    { title: '操作类型', dataIndex: 'opType', key: 'opType',
      render: (op) => <Tag color={op === '新增' ? 'success' : op === '修改' ? 'blue' : 'red'}>{op}</Tag> },
    { title: '操作人', dataIndex: 'srcUser', key: 'srcUser' },
    { title: '操作IP', dataIndex: 'srcIp', key: 'srcIp' },
    { title: 'MD5', dataIndex: 'md5', key: 'md5', ellipsis: true },
    { title: '操作时间', dataIndex: 'gmtCreate', key: 'gmtCreate' },
  ]

  return (
    <div>
      <Card title="变更历史">
        <div style={{ marginBottom: 16 }}>
          <Space size="middle">
            <Select placeholder="选择命名空间" style={{ width: 200 }} value={selectedNamespace}
              onChange={setSelectedNamespace} allowClear options={namespaceList} />
            <Select placeholder="选择Group" style={{ width: 200 }} value={selectedGroup}
              onChange={setSelectedGroup} allowClear options={groupList} />
            <Input.Search placeholder="搜索 Data ID..." value={searchText}
              onChange={(e) => setSearchText(e.target.value)} onSearch={handleSearch}
              style={{ width: 300 }} prefix={<SearchOutlined />} allowClear />
            <Button type="primary" onClick={handleSearch}>搜索</Button>
            <Button icon={<ReloadOutlined />} onClick={() => fetchHistoryList(pagination.current, pagination.pageSize)} loading={loading}>刷新</Button>
          </Space>
        </div>
        <Table columns={columns} dataSource={data}
          pagination={{ ...pagination, showSizeChanger: true, showTotal: (t) => `共 ${t} 条` }}
          loading={loading} onChange={handleTableChange} />
      </Card>
    </div>
  )
}

export default ConfigHistory
