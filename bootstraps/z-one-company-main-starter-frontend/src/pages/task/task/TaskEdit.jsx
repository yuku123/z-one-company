import { Form, Input, Select, DatePicker, Button, Card, message } from 'antd'
import { useNavigate, useParams } from 'react-router-dom'
import TextArea from 'antd/es/input/TextArea'

const { Option } = Select

export default function TaskEdit() {
  const navigate = useNavigate()
  const { id } = useParams()
  const isEdit = id !== 'new'

  const onFinish = (values) => {
    console.log('表单值:', values)
    message.success(isEdit ? '任务更新成功' : '任务创建成功')
    navigate('/task')
  }

  return (
    <Card title={isEdit ? '编辑任务' : '新建任务'}>
      <Form
        layout="vertical"
        onFinish={onFinish}
        initialValues={{
          status: 'pending',
          priority: 'medium'
        }}
      >
        <Form.Item
          name="name"
          label="任务名称"
          rules={[{ required: true, message: '请输入任务名称' }]}
        >
          <Input placeholder="请输入任务名称" />
        </Form.Item>

        <Form.Item
          name="projectId"
          label="所属项目"
          rules={[{ required: true, message: '请选择所属项目' }]}
        >
          <Select placeholder="请选择项目">
            <Option value={1}>Z-Task项目</Option>
            <Option value={2}>Z-Config项目</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="assigneeId"
          label="负责人"
          rules={[{ required: true, message: '请选择负责人' }]}
        >
          <Select placeholder="请选择负责人">
            <Option value={1}>张三</Option>
            <Option value={2}>李四</Option>
            <Option value={3}>王五</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="status"
          label="状态"
          rules={[{ required: true, message: '请选择状态' }]}
        >
          <Select>
            <Option value="pending">待开始</Option>
            <Option value="processing">进行中</Option>
            <Option value="completed">已完成</Option>
            <Option value="cancelled">已取消</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="priority"
          label="优先级"
          rules={[{ required: true, message: '请选择优先级' }]}
        >
          <Select>
            <Option value="low">低</Option>
            <Option value="medium">中</Option>
            <Option value="high">高</Option>
            <Option value="urgent">紧急</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="deadline"
          label="截止日期"
        >
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="description"
          label="任务描述"
        >
          <TextArea rows={4} placeholder="请输入任务描述" />
        </Form.Item>

        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit">
              {isEdit ? '更新' : '创建'}
            </Button>
            <Button onClick={() => navigate('/task')}>
              取消
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  )
}
