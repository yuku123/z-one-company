import { useState, useEffect } from 'react'
import { Card, Table, Button, Input, Space, Tag, message, Popconfirm, Select } from 'antd'
import { PlusOutlined, SearchOutlined, ReloadOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { configApi } from '@/services/api'

const ConfigList = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  })
  const [searchText, setSearchText] = useState('')
  const [selectedNamespace, setSelectedNamespace] = useState(undefined)
  const [selectedGroup, setSelectedGroup] = useState(undefined)
  const [namespaceList, setNamespaceList] = useState([])
  const [groupList, setGroupList] = useState([])

  // 获取命名空间列表（从集群接口）
  const fetchNamespaceList = async () => {
    try {
      const res = await configApi.clusterList()
      if (res.success) {
        setNamespaceList((res.data || []).map(item => ({ label: item.name, value: item.name })))
      }
    } catch (e) {
      console.error('获取命名空间失败', e)
    }
  }

  // 获取Group列表
  const fetchGroupList = async () => {
    try {
      const res = await configApi.groupList()
      if (res.success) {
        setGroupList((res.data || []).map(item => ({ label: item, value: item })))
      }
    } catch (e) {
      console.error('获取Group失败', e)
    }
  }

  // 获取配置列表
  const fetchConfigList = async (page = 1, pageSize = 10) => {
    setLoading(true)
    try {
      const result = await configApi.pageConfig({
        pageNum: page,
        pageSize: pageSize,
        search: searchText || undefined,
        nameSpace: selectedNamespace,
        group: selectedGroup,
      })

      if (result.success && result.data) {
        const records = result.data.records || []
        const total = result.data.total || 0
        const current = result.data.current || 1

        setData(records.map((item, index) => ({
          key: item.id || index,
          ...item,
        })))
        setPagination({
          current: current,
          pageSize: pageSize,
          total: total,
        })
      } else {
        message.error(result.message || '获取配置列表失败')
      }
    } catch (error) {
      console.error('获取配置列表错误:', error)
      message.error('网络错误')
    } finally {
      setLoading(false)
    }
  }

  // 删除配置
  const handleDelete = async (record) => {
    try {
      const result = await configApi.deleteConfig({
        nameSpace: record.namespace,
        group: record.group,
        dataId: record.dataId,
      })
      if (result.success) {
        message.success('删除成功')
        fetchConfigList(pagination.current, pagination.pageSize)
      } else {
        message.error(result.message || '删除失败')
      }
    } catch (error) {
      console.error('删除错误:', error)
      message.error('网络错误')
    }
  }

  const handleEdit = (record) => {
    navigate('/config/edit', { state: { config: record } })
  }

  const handleTableChange = (newPagination) => {
    fetchConfigList(newPagination.current, newPagination.pageSize)
  }

  const handleSearch = () => {
    fetchConfigList(1, pagination.pageSize)
  }

  useEffect(() => {
    fetchConfigList()
    fetchNamespaceList()
    fetchGroupList()
  }, [])

  const columns = [
    { title: 'Data ID', dataIndex: 'dataId', key: 'dataId', ellipsis: true },
    { title: 'Group', dataIndex: 'group', key: 'group' },
    { title: '应用名', dataIndex: 'appName', key: 'appName' },
    { title: '类型', dataIndex: 'configType', key: 'configType',
      render: (type) => <Tag color="blue">{type || 'TEXT'}</Tag> },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (status) => <Tag color={status === 1 ? 'success' : 'default'}>{status === 1 ? '正常' : '停用'}</Tag> },
    { title: '操作', key: 'action', width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除" description={`确定要删除配置 "${record.dataId}" 吗？`}
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
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={() => fetchConfigList(pagination.current, pagination.pageSize)} loading={loading}>刷新</Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/config/edit')}>新建配置</Button>
          </Space>
        }>
        <div style={{ marginBottom: 16 }}>
          <Space size="middle">
            <Select placeholder="选择命名空间" style={{ width: 200 }} value={selectedNamespace}
              onChange={(value) => setSelectedNamespace(value)} allowClear options={namespaceList} />
            <Select placeholder="选择Group" style={{ width: 200 }} value={selectedGroup}
              onChange={(value) => setSelectedGroup(value)} allowClear options={groupList} />
            <Input.Search placeholder="搜索 Data ID..." value={searchText}
              onChange={(e) => setSearchText(e.target.value)} onSearch={handleSearch} style={{ width: 300 }}
              prefix={<SearchOutlined />} allowClear />
            <Button type="primary" onClick={handleSearch}>搜索</Button>
          </Space>
        </div>
        <Table columns={columns} dataSource={data}
          pagination={{ ...pagination, showSizeChanger: true, showTotal: (total) => `共 ${total} 条` }}
          loading={loading} onChange={handleTableChange} />
      </Card>
    </div>
  )
}

export default ConfigList
