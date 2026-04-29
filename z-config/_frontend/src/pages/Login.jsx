import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Form, Input, Button, Card, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import './Login.css'

const Login = () => {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const onFinish = async (values) => {
    setLoading(true)
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(values),
      })

      const data = await response.json()

      if (data.success && data.data) {
        // 保存 token
        localStorage.setItem('zconfig_token', data.data.token)
        localStorage.setItem('zconfig_user', JSON.stringify(data.data))
        message.success('登录成功')
        navigate('/')
      } else {
        message.error(data.message || '登录失败')
      }
    } catch (error) {
      console.error('登录错误:', error)
      message.error('网络错误')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <Card className="login-card" title="Z-Config 配置中心" bordered={false}>
        <p className="login-subtitle">分布式配置管理平台</p>
        <Form
          name="login"
          initialValues={{ username: 'admin', password: 'admin' }}
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
            >
              登录
            </Button>
          </Form.Item>
        </Form>
        <p className="login-hint">默认账号: admin / admin</p>
      </Card>
    </div>
  )
}

export default Login
