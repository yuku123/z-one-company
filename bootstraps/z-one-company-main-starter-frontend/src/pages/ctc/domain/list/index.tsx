import React, { useRef, useState, useEffect } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag } from 'antd';
import { getDomainPage, getTenantListAll, deleteDomain, createDomain, updateDomain } from '@/services/api';
import DomainForm from './components/DomainForm';

const DomainList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentDomain, setCurrentDomain] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');
  const [tenantList, setTenantList] = useState<any[]>([]);

  useEffect(() => {
    loadTenantList();
  }, []);

  const loadTenantList = async () => {
    try {
      const res = await getTenantListAll();
      setTenantList(Array.isArray(res) ? res : []);
    } catch (error) {
      console.error('加载租户列表失败', error);
    }
  };

  const columns: ProColumns<any>[] = [
    {
      title: '域编码',
      dataIndex: 'domainCode',
      key: 'domainCode',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
    },
    {
      title: '域名称',
      dataIndex: 'domainName',
      key: 'domainName',
    },
    {
      title: '所属租户',
      dataIndex: 'tenantId',
      key: 'tenantId',
      render: (_, record) => {
        const tenant = tenantList.find(t => t.id === record.tenantId);
        return <Tag color="blue">{tenant?.tenantName || '-'}</Tag>;
      },
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
      dataIndex: 'createdTime',
      key: 'createdTime',
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
          title="确定删除该域吗？"
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
    setCurrentDomain(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteDomain(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createDomain(values);
        message.success('创建成功');
      } else {
        await updateDomain({ ...currentDomain, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="域管理">
      <ProTable
        headerTitle="域列表"
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
              setCurrentDomain(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建域
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getDomainPage(params);
          return {
            data: response.records || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <DomainForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentDomain}
        type={modalType}
        tenantList={tenantList}
      />
    </PageContainer>
  );
};

export default DomainList;
