import { useState, useEffect, useCallback } from 'react'
import { Card, Input, Select, Button, Tag, Drawer, Modal, Form, message, Tree, Typography, Row, Col, Pagination, Popconfirm, Empty, Spin, Badge, Tooltip } from 'antd'
import {
  SearchOutlined, DownloadOutlined, PlusOutlined, EyeOutlined,
  CloudUploadOutlined, StarOutlined, FireOutlined, CodeOutlined,
  AppstoreOutlined, TagOutlined, UserOutlined, HistoryOutlined,
  SendOutlined
} from '@ant-design/icons'
import { skillApi } from '@/services/api'

const { TextArea } = Input
const { Title, Paragraph, Text } = Typography

const sortOptions = [
  { value: 'download_count', label: '最热门' },
  { value: 'gmt_create', label: '最新发布' },
  { value: 'skill_name', label: '名称排序' },
]

const statusColors = { PUBLISHED: 'green', DRAFT: 'orange', ARCHIVED: 'default' }
const statusLabels = { PUBLISHED: '已发布', DRAFT: '草稿', ARCHIVED: '已归档' }

export default function SkillMarket() {
  // --- state ---
  const [skills, setSkills] = useState([])
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [current, setCurrent] = useState(1)
  const [pageSize] = useState(12)

  const [keyword, setKeyword] = useState('')
  const [categoryCode, setCategoryCode] = useState(undefined)
  const [sortBy, setSortBy] = useState('download_count')

  const [categories, setCategories] = useState([])
  const [expandedKeys, setExpandedKeys] = useState([])

  // detail drawer
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [detailSkill, setDetailSkill] = useState(null)
  const [detailContent, setDetailContent] = useState('')
  const [versions, setVersions] = useState([])

  // publish modal
  const [pubModalOpen, setPubModalOpen] = useState(false)
  const [pubForm] = Form.useForm()
  const [pubLoading, setPubLoading] = useState(false)

  // stats
  const [stats, setStats] = useState({ total: 0, published: 0 })

  // --- load data ---
  const loadSkills = useCallback(async (page = 1) => {
    setLoading(true)
    try {
      const params = { current: page, size: pageSize, sortBy }
      if (keyword) params.keyword = keyword
      if (categoryCode) params.categoryCode = categoryCode
      if (keyword || categoryCode) params.status = 'PUBLISHED'
      const result = await skillApi.page(params)
      if (result) {
        setSkills(result.records || [])
        setTotal(result.total || 0)
      }
    } catch (e) {
      message.error('加载技能列表失败')
    } finally {
      setLoading(false)
    }
  }, [keyword, categoryCode, sortBy, pageSize])

  const loadCategories = useCallback(async () => {
    try {
      const data = await skillApi.categoryTree()
      if (data) {
        const toTree = (list) => list.map(c => ({
          key: c.catCode,
          title: c.catName,
          children: c.children ? toTree(c.children) : undefined,
          isLeaf: !c.children || c.children.length === 0,
        }))
        setCategories(toTree(data))
        setExpandedKeys(data.map(c => c.catCode))
      }
    } catch (e) { /* ignore */ }
  }, [])

  const loadStats = useCallback(async () => {
    try {
      const data = await skillApi.stats()
      if (data) setStats(data)
    } catch (e) { /* ignore */ }
  }, [])

  useEffect(() => { loadSkills(current) }, [current])
  useEffect(() => { setCurrent(1); loadSkills(1) }, [keyword, categoryCode, sortBy])
  useEffect(() => { loadCategories(); loadStats() }, [])

  // --- detail ---
  const openDetail = async (skill) => {
    setDetailSkill(skill)
    setDrawerOpen(true)
    setDetailContent('')
    setVersions([])
    try {
      const [content, verList] = await Promise.all([
        skillApi.getContent(skill.skillCode),
        skillApi.versions(skill.skillCode),
      ])
      setDetailContent(content || '暂无内容')
      setVersions(verList || [])
    } catch (e) { /* ignore */ }
  }

  // --- install ---
  const handleInstall = async (skillCode) => {
    try {
      await skillApi.install({ skillCode, installedBy: 'admin', tenantCode: 'admin' })
      message.success('安装成功！技能已添加到你的工作区')
      loadSkills(current)
    } catch (e) {
      message.error('安装失败')
    }
  }

  // --- publish ---
  const handlePublish = async () => {
    try {
      const values = await pubForm.validateFields()
      setPubLoading(true)
      await skillApi.create(values)
      message.success('技能发布成功！可在市场中搜索到')
      setPubModalOpen(false)
      pubForm.resetFields()
      loadSkills(1)
      loadStats()
    } catch (e) {
      if (e.errorFields) return
      message.error('发布失败')
    } finally {
      setPubLoading(false)
    }
  }

  // --- helpers ---
  const tagColors = ['blue', 'green', 'cyan', 'geekblue', 'purple', 'magenta', 'orange', 'gold']

  // ==================== RENDER ====================
  return (
    <div style={{ height: 'calc(100vh - 180px)', display: 'flex', gap: 16 }}>
      {/* ===== LEFT: Category Tree ===== */}
      <Card
        size="small"
        title={<span><AppstoreOutlined /> 分类</span>}
        style={{ width: 220, flexShrink: 0, overflow: 'auto' }}
      >
        <Tree
          showIcon={false}
          treeData={categories}
          expandedKeys={expandedKeys}
          onExpand={setExpandedKeys}
          selectedKeys={categoryCode ? [categoryCode] : []}
          onSelect={(keys) => {
            if (keys.length === 0) setCategoryCode(undefined)
            else setCategoryCode(keys[0])
          }}
          style={{ marginTop: -8 }}
        />
        {categoryCode && (
          <Button type="link" size="small" onClick={() => setCategoryCode(undefined)}
            style={{ padding: 0, marginTop: 4 }}>
            清除筛选
          </Button>
        )}
      </Card>

      {/* ===== RIGHT: Content ===== */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 12, minWidth: 0 }}>
        {/* Toolbar */}
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
          <Input
            prefix={<SearchOutlined />}
            placeholder="搜索技能名称、描述、标签..."
            value={keyword}
            onChange={e => setKeyword(e.target.value)}
            allowClear
            style={{ width: 320 }}
          />
          <Select
            value={sortBy}
            onChange={setSortBy}
            options={sortOptions}
            style={{ width: 120 }}
          />
          <div style={{ flex: 1 }} />
          <Text type="secondary">
            <FireOutlined style={{ color: '#ff4d4f', marginRight: 4 }} />
            {stats.total || 0} 个技能
          </Text>
          <Button type="primary" icon={<CloudUploadOutlined />} onClick={() => setPubModalOpen(true)}>
            发布技能
          </Button>
        </div>

        {/* Skill Cards Grid */}
        <Spin spinning={loading}>
          <div style={{ flex: 1, overflow: 'auto' }}>
            {skills.length === 0 && !loading ? (
              <Empty description="暂无技能，快来发布第一个吧！" style={{ marginTop: 80 }} />
            ) : (
              <Row gutter={[16, 16]}>
                {skills.map(skill => {
                  const tagList = skill.tags ? skill.tags.split(',').filter(Boolean) : []
                  return (
                    <Col key={skill.id} xs={24} sm={12} md={8} lg={6}>
                      <Badge.Ribbon
                        text={statusLabels[skill.status] || skill.status}
                        color={statusColors[skill.status] || 'default'}
                        style={{ display: skill.status !== 'PUBLISHED' ? undefined : 'none' }}
                      >
                        <Card
                          hoverable
                          size="small"
                          onClick={() => openDetail(skill)}
                          style={{ height: 200, display: 'flex', flexDirection: 'column' }}
                          bodyStyle={{ flex: 1, display: 'flex', flexDirection: 'column', padding: 12 }}
                        >
                          {/* Header */}
                          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                            <div style={{
                              width: 36, height: 36, borderRadius: 8,
                              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                              display: 'flex', alignItems: 'center', justifyContent: 'center',
                              flexShrink: 0,
                            }}>
                              <CodeOutlined style={{ fontSize: 18, color: '#fff' }} />
                            </div>
                            <div style={{ flex: 1, minWidth: 0 }}>
                              <Text strong ellipsis style={{ display: 'block', fontSize: 14 }}>
                                {skill.skillName}
                              </Text>
                              <Text type="secondary" style={{ fontSize: 12 }}>
                                v{skill.version} · {skill.author}
                              </Text>
                            </div>
                          </div>

                          {/* Description */}
                          <Paragraph
                            ellipsis={{ rows: 2 }}
                            style={{ flex: 1, marginBottom: 8, fontSize: 12, color: '#666' }}
                          >
                            {skill.description || '暂无描述'}
                          </Paragraph>

                          {/* Tags */}
                          <div style={{ marginBottom: 8 }}>
                            {tagList.slice(0, 3).map((tag, i) => (
                              <Tag key={tag} color={tagColors[i % tagColors.length]} style={{ fontSize: 11, marginBottom: 2 }}>
                                {tag}
                              </Tag>
                            ))}
                            {tagList.length > 3 && (
                              <Tag style={{ fontSize: 11 }}>+{tagList.length - 3}</Tag>
                            )}
                          </div>

                          {/* Footer */}
                          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                            <Tooltip title="安装次数">
                              <Text type="secondary" style={{ fontSize: 12 }}>
                                <DownloadOutlined /> {skill.downloadCount || 0}
                              </Text>
                            </Tooltip>
                            <Button
                              type="primary"
                              size="small"
                              icon={<DownloadOutlined />}
                              onClick={e => { e.stopPropagation(); handleInstall(skill.skillCode) }}
                            >
                              安装
                            </Button>
                          </div>
                        </Card>
                      </Badge.Ribbon>
                    </Col>
                  )
                })}
              </Row>
            )}
          </div>
        </Spin>

        {/* Pagination */}
        {total > pageSize && (
          <div style={{ textAlign: 'center', paddingTop: 8 }}>
            <Pagination
              current={current}
              total={total}
              pageSize={pageSize}
              onChange={page => setCurrent(page)}
              showSizeChanger={false}
              showTotal={t => `共 ${t} 个技能`}
            />
          </div>
        )}
      </div>

      {/* ===== Detail Drawer ===== */}
      <Drawer
        title={detailSkill?.skillName || '技能详情'}
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={640}
        extra={
          detailSkill && (
            <Button type="primary" icon={<DownloadOutlined />}
              onClick={() => handleInstall(detailSkill.skillCode)}>
              安装此技能
            </Button>
          )
        }
      >
        {detailSkill && (
          <div>
            {/* Meta */}
            <div style={{ marginBottom: 16, display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'center' }}>
              <Tag color="blue">v{detailSkill.version}</Tag>
              <Text type="secondary"><UserOutlined /> {detailSkill.author}</Text>
              <Text type="secondary"><DownloadOutlined /> {detailSkill.downloadCount || 0} 次安装</Text>
              <Tag color={statusColors[detailSkill.status]}>{statusLabels[detailSkill.status]}</Tag>
            </div>

            <Paragraph style={{ color: '#666', marginBottom: 16 }}>
              {detailSkill.description}
            </Paragraph>

            {/* Tags */}
            {detailSkill.tags && (
              <div style={{ marginBottom: 16 }}>
                {(detailSkill.tags.split(',')).filter(Boolean).map((tag, i) => (
                  <Tag key={tag} color={tagColors[i % tagColors.length]}>{tag}</Tag>
                ))}
              </div>
            )}

            {/* Content */}
            <Card size="small" title={<span><CodeOutlined /> 技能内容</span>} style={{ marginBottom: 16 }}>
              <pre style={{
                whiteSpace: 'pre-wrap', wordBreak: 'break-word',
                fontSize: 13, lineHeight: 1.6, maxHeight: 400, overflow: 'auto',
                background: '#f6f8fa', padding: 12, borderRadius: 6,
                fontFamily: 'SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace',
              }}>
                {detailContent}
              </pre>
            </Card>

            {/* Versions */}
            {versions.length > 0 && (
              <Card size="small" title={<span><HistoryOutlined /> 版本历史 ({versions.length})</span>}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                  {versions.map(v => (
                    <div key={v.id} style={{
                      padding: '8px 12px', background: '#fafafa', borderRadius: 6,
                      borderLeft: '3px solid #1677ff',
                    }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Text strong>v{v.version}</Text>
                        <Text type="secondary" style={{ fontSize: 12 }}>{v.gmtCreate}</Text>
                      </div>
                      {v.changeLog && <Text type="secondary" style={{ fontSize: 12 }}>{v.changeLog}</Text>}
                    </div>
                  ))}
                </div>
              </Card>
            )}
          </div>
        )}
      </Drawer>

      {/* ===== Publish Modal ===== */}
      <Modal
        title="发布新技能"
        open={pubModalOpen}
        onCancel={() => { setPubModalOpen(false); pubForm.resetFields() }}
        onOk={handlePublish}
        confirmLoading={pubLoading}
        okText="发布"
        cancelText="取消"
        width={640}
      >
        <Form form={pubForm} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="skillCode" label="技能编码" rules={[{ required: true, message: '请输入技能编码（英文标识）' }]}>
            <Input placeholder="如 java-entity-gen" />
          </Form.Item>
          <Form.Item name="skillName" label="技能名称" rules={[{ required: true, message: '请输入技能名称' }]}>
            <Input placeholder="如 Java Entity 代码生成器" />
          </Form.Item>
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="categoryCode" label="分类" style={{ flex: 1 }}>
              <Select
                placeholder="选择分类"
                allowClear
                options={categories.flatMap(c => [
                  { value: c.key, label: c.title },
                  ...(c.children || []).map(ch => ({ value: ch.key, label: `  ${ch.title}` })),
                ])}
              />
            </Form.Item>
            <Form.Item name="version" label="版本" initialValue="1.0.0" style={{ flex: 1 }}>
              <Input placeholder="1.0.0" />
            </Form.Item>
          </div>
          <Form.Item name="tags" label="标签">
            <Select mode="tags" placeholder="输入标签后回车（如 java, mybatis）" />
          </Form.Item>
          <Form.Item name="description" label="描述" rules={[{ required: true, message: '请输入技能描述' }]}>
            <TextArea rows={2} placeholder="简要描述这个技能的功能..." />
          </Form.Item>
          <Form.Item name="content" label="技能内容 (Markdown)" rules={[{ required: true, message: '请输入技能内容' }]}>
            <TextArea rows={10} placeholder={`# 技能名称\n\n## 功能描述\n\n## 使用方法\n\n## 示例\n`} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
