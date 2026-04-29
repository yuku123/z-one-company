import React, { useRef, useState } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag } from 'antd';
import { getTenantPage, deleteTenant, createTenant, updateTenant } from '@/services/api';
import TenantForm from './components/TenantForm';

const TenantList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentTenant, setCurrentTenant] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');

  const columns: ProColumns<any>[] = [
    {
      title: '租户编码',
      dataIndex: 'tenantCode',
      key: 'tenantCode',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
    },
    {
      title: '租户名称',
      dataIndex: 'tenantName',
      key: 'tenantName',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      valueEnum: {
        1: { text: '启用', status: 'Success' },
        0: { text: '禁用', status: 'Error' },
      },
    },
    {
      title: '创建时间',
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
        <a key="edit" onClick={() => handleEdit(record)}>
          编辑
        </a>,
        <Popconfirm
          key="delete"
          title="确定删除该租户吗？"
          onConfirm={() => handleDelete(record)}
          okText="确定"
          cancelText="取消"
        >
          <a style={{ color: '#ff4d4f' }}>删除</a>
        </Popconfirm>,
      ],
    },
  ];

  const handleEdit = (record: any) => {
    setCurrentTenant(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteTenant(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createTenant(values);
        message.success('创建成功');
      } else {
        await updateTenant({ ...currentTenant, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="租户管理">
      <ProTable
        headerTitle="租户列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCurrentTenant(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建租户
          </Button>,
        ]}
        request={async (params) => {
          console.log('请求参数:', params);
          const response: any = await getTenantPage(params);
          console.log('响应数据:', response);
          return {
            data: response.records || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <TenantForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentTenant}
        type={modalType}
      />
    </PageContainer>
  );
};

export default TenantList;
