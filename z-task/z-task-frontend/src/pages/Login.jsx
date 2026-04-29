import { Form, Input, Button, Card, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import request from '../utils/request'
import './Login.css'

export default function Login() {
  const navigate = useNavigate()

  const onFinish = async (values) => {
    try {
      const res = await request.post('/user/login', values)
      localStorage.setItem('token', res.token)
      message.success('登录成功')
      navigate('/')
    } catch (error) {
      message.error('登录失败：' + (error.response?.data?.message || error.message))
    }
  }

  return (
    <div className="login-container">
      <Card title="欢迎登录 Z-Task" className="login-card">
        <Form
          name="login"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          autoComplete="off"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名!' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="用户名" size="large" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码!' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              size="large"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" size="large" block>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}
