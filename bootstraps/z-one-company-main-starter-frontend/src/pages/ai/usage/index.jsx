import { useState, useEffect } from 'react'
import { Row, Col, Card, Statistic, Table, DatePicker, Select, Space, Tag, Button, Tabs, List, Typography } from 'antd'
import { BarChartOutlined, DollarOutlined, RocketOutlined, UserOutlined, AppstoreOutlined, LineChartOutlined } from '@ant-design/icons'
import { request } from 'umi'
import ReactECharts from 'echarts-for-react'

const { RangePicker } = DatePicker
const { Title, Text } = Typography
const { TabPane } = Tabs

const UsageDashboard = () => {
  const [overview, setOverview] = useState(null)
  const [byApp, setByApp] = useState([])
  const [byUser, setByUser] = useState([])
  const [trend, setTrend] = useState([])
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(false)
  const [dateRange, setDateRange] = useState([])
  const [appFilter, setAppFilter] = useState(null)

  const fetchOverview = async () => {
    try {
      const res = await request('/api/llm-gateway/usage/overview', {
        method: 'GET',
        params: { appCode: appFilter }
      })
      setOverview(res.data)
    } catch (e) {}
  }

  const fetchByApp = async () => {
    try {
      const res = await request('/api/llm-gateway/usage/by-app', { method: 'GET' })
      setByApp(res.data || [])
    } catch (e) {}
  }

  const fetchByUser = async () => {
    try {
      const res = await request('/api/llm-gateway/usage/by-user', {
        method: 'GET',
        params: { appCode: appFilter }
      })
      setByUser(res.data || [])
    } catch (e) {}
  }

  const fetchTrend = async () => {
    try {
      const res = await request('/api/llm-gateway/usage/trend', {
        method: 'GET',
        params: { appCode: appFilter, days: 7 }
      })
      setTrend(res.data || [])
    } catch (e) {}
  }

  const fetchRecords = async () => {
    setLoading(true)
    try {
      const res = await request('/api/llm-gateway/usage/records', {
        method: 'GET',
        params: { appCode: appFilter, page: 1, size: 20 }
      })
      setRecords(res.data?.records || [])
    } catch (e) {
      message.error('加载失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchOverview()
    fetchByApp()
    fetchByUser()
    fetchTrend()
    fetchRecords()
  }, [appFilter])

  // 趋势图配置
  const trendOption = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['Token消耗', '调用次数'] },
    xAxis: {
      type: 'category',
      data: trend.map(t => t.date)
    },
    yAxis: [
      { type: 'value', name: 'Tokens', position: 'left' },
      { type: 'value', name: '调用次数', position: 'right' }
    ],
    series: [
      {
        name: 'Token消耗',
        type: 'bar',
        data: trend.map(t => t.dailyTokens)
      },
      {
        name: '调用次数',
        type: 'line',
        yAxisIndex: 1,
        data: trend.map(t => t.dailyCalls)
      }
    ]
  }

  const appColumns = [
    { title: '应用编码', dataIndex: 'appCode' },
    { title: '调用次数', dataIndex: 'totalCalls', render: v => v?.toLocaleString() },
    { title: '输入Token', dataIndex: 'inputTokens', render: v => v?.toLocaleString() },
    { title: '输出Token', dataIndex: 'outputTokens', render: v => v?.toLocaleString() },
    { title: '总Token', dataIndex: 'totalTokens', render: v => v?.toLocaleString() },
    {
      title: '费用',
      dataIndex: 'totalCost',
      render: v => <Text style={{ color: '#faad14' }}>¥{v?.toFixed(4) || '0'}</Text>
    }
  ]

  const userColumns = [
    { title: '用户ID', dataIndex: 'userId' },
    { title: '调用次数', dataIndex: 'totalCalls', render: v => v?.toLocaleString() },
    { title: '总Token', dataIndex: 'totalTokens', render: v => v?.toLocaleString() },
    {
      title: '费用',
      dataIndex: 'totalCost',
      render: v => <Text style={{ color: '#faad14' }}>¥{v?.toFixed(4) || '0'}</Text>
    }
  ]

  const recordColumns = [
    { title: '时间', dataIndex: 'gmtCreate', width: 160 },
    { title: '应用', dataIndex: 'appCode', render: v => <Tag>{v}</Tag> },
    { title: '用户', dataIndex: 'userName' },
    { title: '模型', dataIndex: 'modelCode', render: v => <Tag>{v}</Tag> },
    { title: '输入Token', dataIndex: 'inputTokens' },
    { title: '输出Token', dataIndex: 'outputTokens' },
    { title: '延迟', dataIndex: 'latencyMs', render: v => `${v}ms` },
    {
      title: '状态',
      dataIndex: 'status',
      render: v => <Tag color={v === 'SUCCESS' ? 'green' : 'red'}>{v}</Tag>
    },
    {
      title: '费用',
      dataIndex: 'totalCost',
      render: v => <Text style={{ color: '#faad14' }}>¥{v || 0}</Text>
    }
  ]

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Select
            placeholder="筛选应用"
            allowClear
            style={{ width: 200 }}
            onChange={setAppFilter}
          >
            <Select.Option value="app1">App 1</Select.Option>
            <Select.Option value="app2">App 2</Select.Option>
          </Select>
        </Space>
      </div>

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="累计 Token"
              value={overview?.totalTokens || 0}
              prefix={<RocketOutlined />}
              formatter={v => v?.toLocaleString()}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="累计费用"
              value={overview?.totalCost || 0}
              prefix={<DollarOutlined />}
              precision={4}
              prefix="¥"
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日 Token"
              value={overview?.todayTokens || 0}
              prefix={<BarChartOutlined />}
              formatter={v => v?.toLocaleString()}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日费用"
              value={overview?.todayCost || 0}
              prefix={<DollarOutlined />}
              precision={4}
              prefix="¥"
            />
          </Card>
        </Col>
      </Row>

      <Tabs defaultActiveKey="trend">
        <TabPane tab="用量趋势" key="trend">
          <Card>
            <ReactECharts option={trendOption} style={{ height: 300 }} />
          </Card>
        </TabPane>

        <TabPane tab="按应用统计" key="byApp">
          <Card>
            <Table columns={appColumns} dataSource={byApp} rowKey="appCode" pagination={false} />
          </Card>
        </TabPane>

        <TabPane tab="按用户统计" key="byUser">
          <Card>
            <Table columns={userColumns} dataSource={byUser} rowKey="userId" pagination={false} />
          </Card>
        </TabPane>

        <TabPane tab="调用明细" key="records">
          <Card>
            <Table columns={recordColumns} dataSource={records} loading={loading} rowKey="id" />
          </Card>
        </TabPane>
      </Tabs>
    </div>
  )
}

export default UsageDashboard
