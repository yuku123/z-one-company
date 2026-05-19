import { useState, useEffect, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { Card, Input, Button, List, Avatar, Spin, message } from 'antd'
import { SendOutlined, ClearOutlined, RobotOutlined, UserOutlined } from '@ant-design/icons'
import { agentApi } from '../../../services/api'

const { TextArea } = Input

const AgentSharePage = () => {
  const { shareCode } = useParams()
  const [loading, setLoading] = useState(true)
  const [instanceCode, setInstanceCode] = useState('')
  const [appName, setAppName] = useState('')
  const [messages, setMessages] = useState([])
  const [inputValue, setInputValue] = useState('')
  const [sending, setSending] = useState(false)
  const [chatHistory, setChatHistory] = useState([])
  const bottomRef = useRef(null)

  useEffect(() => {
    verifyAndLoad()
  }, [shareCode])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const verifyAndLoad = async () => {
    setLoading(true)
    try {
      const res = await agentApi.shareVerify(shareCode)
      if (res.data) {
        setInstanceCode(res.data.instanceCode)
        setAppName(res.data.appCode)
        // 加载历史消息
        const historyRes = await agentApi.chatHistory(res.data.instanceCode, 50)
        if (historyRes.data) {
          const historyMsgs = historyRes.data.map(h => ({
            id: h.id,
            role: 'user',
            content: h.userMessage,
            time: h.gmtCreate,
          }, {
            id: h.id + '_a',
            role: 'assistant',
            content: h.assistantMessage,
            time: h.gmtCreate,
          })).flat().sort((a, b) => new Date(a.time) - new Date(b.time))
          setMessages(historyMsgs)
        }
      }
    } catch (e) {
      message.error('分享链接无效或已过期')
    }
    setLoading(false)
  }

  const handleSend = async () => {
    if (!inputValue.trim() || sending) return

    const userMsg = { id: Date.now(), role: 'user', content: inputValue, time: new Date().toISOString() }
    setMessages(prev => [...prev, userMsg])
    setInputValue('')
    setSending(true)

    try {
      const res = await agentApi.chatSend({
        instanceCode,
        message: userMsg.content,
        userId: 'visitor',
        userName: '游客',
      })
      if (res.data) {
        const assistantMsg = {
          id: Date.now() + 1,
          role: 'assistant',
          content: res.data.assistantMessage,
          time: new Date().toISOString(),
        }
        setMessages(prev => [...prev, assistantMsg])
      }
    } catch (e) {
      message.error('发送失败')
    }
    setSending(false)
  }

  const handleClear = () => {
    setMessages([])
    agentApi.chatClear(instanceCode)
  }

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" tip="加载中..." />
      </div>
    )
  }

  return (
    <div style={{ height: '100vh', display: 'flex', flexDirection: 'column', background: '#f0f2f5' }}>
      {/* Header */}
      <Card size="small" style={{ borderRadius: 0, borderBottom: '1px solid #e8e8e8' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <RobotOutlined style={{ fontSize: 20, color: '#1890ff' }} />
          <span style={{ fontSize: 16, fontWeight: 500 }}>{appName || 'Agent'}</span>
        </div>
      </Card>

      {/* Chat Area */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '16px' }}>
        {messages.length === 0 ? (
          <div style={{ textAlign: 'center', color: '#999', marginTop: 100 }}>
            <RobotOutlined style={{ fontSize: 48, marginBottom: 16 }} />
            <p>开始对话吧~</p>
          </div>
        ) : (
          <List dataSource={messages} renderItem={(item) => (
            <List.Item style={{ justifyContent: item.role === 'user' ? 'flex-end' : 'flex-start', border: 'none', padding: '8px 0' }}>
              <div style={{
                maxWidth: '70%',
                display: 'flex',
                flexDirection: item.role === 'user' ? 'row-reverse' : 'row',
                alignItems: 'flex-start',
                gap: 8,
              }}>
                <Avatar icon={item.role === 'user' ? <UserOutlined /> : <RobotOutlined />} style={{ background: item.role === 'user' ? '#1890ff' : '#52c41a' }} />
                <div style={{
                  padding: '10px 14px',
                  borderRadius: 8,
                  background: item.role === 'user' ? '#1890ff' : '#fff',
                  color: item.role === 'user' ? '#fff' : '#333',
                  boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
                }}>
                  {item.content}
                </div>
              </div>
            </List.Item>
          )} />
        )}
        <div ref={bottomRef} />
      </div>

      {/* Input Area */}
      <Card size="small" style={{ borderRadius: 0, borderTop: '1px solid #e8e8e8' }}>
        <div style={{ display: 'flex', gap: 8 }}>
          <TextArea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onPressEnter={(e) => { if (!e.shiftKey) { e.preventDefault(); handleSend(); } }}
            placeholder="输入消息，Enter发送，Shift+Enter换行"
            autoSize={{ minRows: 1, maxRows: 4 }}
            style={{ flex: 1 }}
          />
          <Button type="primary" icon={<SendOutlined />} onClick={handleSend} loading={sending}>发送</Button>
          <Button icon={<ClearOutlined />} onClick={handleClear}>清空</Button>
        </div>
      </Card>
    </div>
  )
}

export default AgentSharePage
