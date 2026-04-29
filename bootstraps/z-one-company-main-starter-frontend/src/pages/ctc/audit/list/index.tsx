import React, { useRef, useState } from 'react';
import { DownloadOutlined, EyeOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Drawer, Tag, message, Descriptions } from 'antd';
import { getAuditList, exportAudit } from '@/services/api';

const AuditList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<any>(null);

  const columns: ProColumns<any>[] = [
    {
      title: '操作人',
      dataIndex: 'operatorName',
      key: 'operatorName',
    },
    {
      title: '操作类型',
      dataIndex: 'operationType',
      key: 'operationType',
      render: (_, record) => {
        const typeMap: Record<string, { color: string; text: string }> = {
          CREATE: { color: 'green', text: '创建' },
          UPDATE: { color: 'blue', text: '更新' },
          DELETE: { color: 'red', text: '删除' },
          LOGIN: { color: 'cyan', text: '登录' },
          LOGOUT: { color: 'orange', text: '登出' },
          EXPORT: { color: 'purple', text: '导出' },
          IMPORT: { color: 'magenta', text: '导入' },
        };
        const config = typeMap[record.operationType] || { color: 'default', text: record.operationType };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '操作对象',
      dataIndex: 'targetType',
      key: 'targetType',
      render: (_, record) => `${record.targetType}:${record.targetId}`,
    },
    {
      title: '操作描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: 'IP地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
    },
    {
      title: '操作时间',
      dataIndex: 'gmtCreate',
      key: 'gmtCreate',
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '操作',
      valueType: 'option',
      key: 'option',
      render: (_, record) => [
        <Button
          key="view"
          type="link"
          icon={<EyeOutlined />}
          onClick={() => handleViewDetail(record)}
        >
          详情
        </Button>,
      ],
    },
  ];

  const handleViewDetail = (record: any) => {
    setCurrentRecord(record);
    setDetailVisible(true);
  };

  const handleExport = async () => {
    try {
      await exportAudit();
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败');
    }
  };

  return (
    <PageContainer title="审计日志">
      <ProTable
        headerTitle="操作日志"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            key="export"
            icon={<DownloadOutlined />}
            onClick={handleExport}
          >
            导出
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getAuditList(params);
          return {
            data: response.data || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <Drawer
        title="操作详情"
        width={600}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {currentRecord && (
          <>
            <Descriptions title="基本信息" column={1} bordered>
              <Descriptions.Item label="操作人">{currentRecord.operatorName}</Descriptions.Item>
              <Descriptions.Item label="操作类型">{currentRecord.operationType}</Descriptions.Item>
              <Descriptions.Item label="操作对象">{currentRecord.targetType}:{currentRecord.targetId}</Descriptions.Item>
              <Descriptions.Item label="操作描述">{currentRecord.description}</Descriptions.Item>
              <Descriptions.Item label="IP地址">{currentRecord.ipAddress}</Descriptions.Item>
              <Descriptions.Item label="操作时间">{currentRecord.gmtCreate}</Descriptions.Item>
            </Descriptions>
            {currentRecord.beforeValue && (
              <Descriptions title="变更前数据" column={1} bordered style={{ marginTop: 24 }}>
                <Descriptions.Item>
                  <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>
                    {JSON.stringify(JSON.parse(currentRecord.beforeValue), null, 2)}
                  </pre>
                </Descriptions.Item>
              </Descriptions>
            )}
            {currentRecord.afterValue && (
              <Descriptions title="变更后数据" column={1} bordered style={{ marginTop: 24 }}>
                <Descriptions.Item>
                  <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>
                    {JSON.stringify(JSON.parse(currentRecord.afterValue), null, 2)}
                  </pre>
                </Descriptions.Item>
              </Descriptions>
            )}
          </>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default AuditList;
