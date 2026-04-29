import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Card,
  Tabs,
  Form,
  Input,
  Button,
  message,
  Checkbox,
  Typography,
} from 'antd'
import { LockOutlined, UserOutlined, MobileOutlined, MailOutlined } from '@ant-design/icons'
import { authRequest } from '../../../services/request'
import styles from './AuthPage.module.css'

const { Title, Text } = Typography

type LoginType = 'account' | 'phone' | 'email'

// 验证码请求
const sendCode = async (receiver: string, codeType: string, bizType: string) => {
  const url = bizType === 'register'
    ? '/auth/register/send-code'
    : '/auth/reset-password/send-code'
  return authRequest.post(`${url}?receiver=${receiver}&codeType=${codeType}`)
}

// 登录请求
const loginByUsername = (data: { userName: string; password: string }) =>
  authRequest.post('/auth/login', data)

// 用户名注册
const registerByUsername = (data: { username: string; password: string }) =>
  authRequest.post('/auth/register/username', data)

// 手机登录
const loginByPhone = (data: { phone: string; code: string }) =>
  authRequest.post('/auth/login/phone', data)

// 手机注册
const registerByPhone = (data: { phone: string; code: string; password: string }) =>
  authRequest.post('/auth/register/phone', data)

// 邮箱注册
const registerByEmail = (data: { email: string; code: string; password: string }) =>
  authRequest.post('/auth/register/email', data)

const AuthPage: React.FC = () => {
  const navigate = useNavigate()
  const [action, setAction] = useState<'login' | 'register'>('login')
  const [type, setType] = useState<LoginType>('account')
  const [sendingCode, setSendingCode] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const [form] = Form.useForm()

  // 发送验证码
  const handleSendCode = async () => {
    try {
      const receiver = form.getFieldValue('receiver')
      if (!receiver) {
        message.error('请输入手机号或邮箱')
        return
      }
      const codeType = type === 'phone' || (type === 'account' && action === 'login') ? 'PHONE' : 'EMAIL'
      const bizType = action === 'register' ? 'register' : 'forgot'

      setSendingCode(true)
      const res: any = await sendCode(receiver, codeType, bizType)
      if (res.code === 0) {
        message.success('验证码已发送')
        // 开始倒计时
        let seconds = 60
        setCountdown(seconds)
        const timer = setInterval(() => {
          seconds -= 1
          setCountdown(seconds)
          if (seconds <= 0) clearInterval(timer)
        }, 1000)
      } else {
        message.error(res.message || '发送失败')
      }
    } catch (error: any) {
      message.error(error.message || '发送失败')
    } finally {
      setSendingCode(false)
    }
  }

  // 处理登录/注册提交
  const handleSubmit = async (values: any) => {
    try {
      let response: any

      if (action === 'login') {
        if (type === 'account') {
          response = await loginByUsername({ userName: values.username, password: values.password })
        } else if (type === 'phone') {
          response = await loginByPhone({ phone: values.receiver, code: values.code })
        }
      } else if (action === 'register') {
        if (type === 'account') {
          response = await registerByUsername({ username: values.username, password: values.password })
        } else if (type === 'phone') {
          response = await registerByPhone({ phone: values.receiver, code: values.code, password: values.password })
        } else if (type === 'email') {
          response = await registerByEmail({ email: values.receiver, code: values.code, password: values.password })
        }
      }

      if (response && response.token) {
        message.success(action === 'register' ? '注册成功！' : '登录成功！')
        // 保存token和用户信息
        localStorage.setItem('token', response.token)
        localStorage.setItem('userInfo', JSON.stringify({
          userId: response.userId,
          userName: response.userName
        }))
        // 跳转到概览页
        navigate('/ctc/overview')
      } else {
        message.error(response?.message || '操作失败')
      }
    } catch (error: any) {
      message.error(error.message || '操作失败，请重试')
    }
  }

  // Tab配置
  const getTabItems = () => {
    const items = [
      { key: 'account', label: '账户密码' },
    ]

    if (action === 'register') {
      items.push({ key: 'phone', label: '手机注册' })
      items.push({ key: 'email', label: '邮箱注册' })
    } else {
      items.push({ key: 'phone', label: '手机验证码' })
    }

    return items
  }

  // 获取表单项
  const getFormItems = () => {
    const isLogin = action === 'login'

    return (
      <>
        {type === 'account' && (
          <>
            <Form.Item name="username" rules={[{ required: true, message: '请输入用户名!' }]}>
              <Input
                size="large"
                prefix={<UserOutlined />}
                placeholder="用户名"
              />
            </Form.Item>
            <Form.Item name="password" rules={[{ required: true, message: '请输入密码!' }]}>
              <Input.Password
                size="large"
                prefix={<LockOutlined />}
                placeholder="密码"
              />
            </Form.Item>
          </>
        )}

        {(type === 'phone' || type === 'email') && (
          <>
            <Form.Item
              name="receiver"
              rules={[
                { required: true, message: type === 'phone' ? '请输入手机号!' : '请输入邮箱!' }
              ]}
            >
              <Input
                size="large"
                prefix={type === 'phone' ? <MobileOutlined /> : <MailOutlined />}
                placeholder={type === 'phone' ? '手机号' : '邮箱'}
                disabled={countdown > 0}
              />
            </Form.Item>
            <Form.Item>
              <div style={{ display: 'flex', gap: 8 }}>
                <Input
                  size="large"
                  prefix={<LockOutlined />}
                  placeholder="验证码"
                  style={{ flex: 1 }}
                />
                <Button
                  type="primary"
                  onClick={handleSendCode}
                  loading={sendingCode}
                  disabled={countdown > 0}
                  style={{ minWidth: 100 }}
                >
                  {countdown > 0 ? `${countdown}秒` : '获取验证码'}
                </Button>
              </div>
            </Form.Item>
            {!isLogin && (
              <Form.Item name="password" rules={[{ required: true, message: '请输入密码!' }]}>
                <Input.Password
                  size="large"
                  prefix={<LockOutlined />}
                  placeholder="设置密码"
                />
              </Form.Item>
            )}
          </>
        )}
      </>
    )
  }

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <Card className={styles.card}>
          <div className={styles.header}>
            <Title level={3} style={{ margin: 0 }}>
              {action === 'register' ? '用户注册' : 'CTC 组织管理中心'}
            </Title>
            <Text type="secondary">
              {action === 'register' ? '创建新账户' : '4A + SSO 统一身份认证平台'}
            </Text>
          </div>

          <Form
            form={form}
            layout="vertical"
            onFinish={handleSubmit}
            initialValues={{ autoLogin: true }}
          >
            {action !== 'forgot' && (
              <Tabs
                activeKey={type}
                onChange={(key) => setType(key as LoginType)}
                centered
                items={getTabItems()}
              />
            )}

            {getFormItems()}

            <Form.Item>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                {action === 'login' && (
                  <Form.Item name="autoLogin" valuePropName="checked" noStyle>
                    <Checkbox>自动登录</Checkbox>
                  </Form.Item>
                )}
                <a
                  onClick={() => {
                    setAction(action === 'login' ? 'register' : 'login')
                  }}
                >
                  {action === 'login'
                    ? '没有账号？立即注册'
                    : '已有账号？立即登录'}
                </a>
              </div>
            </Form.Item>

            <Form.Item>
              <Button type="primary" htmlType="submit" block size="large">
                {action === 'login' ? '登录' : '注册'}
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  )
}

export default AuthPage