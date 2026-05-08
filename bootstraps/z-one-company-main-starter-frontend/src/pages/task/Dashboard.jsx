import { useState, useEffect } from 'react'
import { Card, Row, Col, Statistic } from 'antd'
import {
  FileTextOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined
} from '@ant-design/icons'
import { taskApi } from '@/services/api'

export default function Dashboard() {
  const [stats, setStats] = useState({
    total: 0,
    todo: 0,
    doing: 0,
    done: 0
  })

  useEffect(() => {
    const userId = localStorage.getItem('userId') || '1'
    taskApi.getTaskListByAssignee(userId)
      .then(data => {
        const tasks = data || []
        setStats({
          total: tasks.length,
          todo: tasks.filter(t => t.status === 0).length,
          doing: tasks.filter(t => t.status === 1).length,
          done: tasks.filter(t => t.status === 2).length
        })
      })
      .catch(() => {
        // 静默失败，保持 0
      })
  }, [])

  return (
    <div>
      <h1 style={{ marginBottom: 24 }}>仪表盘</h1>
      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总任务数"
              value={stats.total}
              prefix={<FileTextOutlined style={{ color: '#1890ff' }} />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待办"
              value={stats.todo}
              prefix={<ClockCircleOutlined style={{ color: '#faad14' }} />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="进行中"
              value={stats.doing}
              prefix={<FileTextOutlined style={{ color: '#1890ff' }} />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已完成"
              value={stats.done}
              prefix={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}
