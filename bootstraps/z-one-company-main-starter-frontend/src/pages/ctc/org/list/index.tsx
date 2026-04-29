import React, { useRef, useState, useEffect } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag } from 'antd';
import { getOrgPage, getTenantListAll, getDomainListAll, deleteOrg, createOrg, updateOrg } from '@/services/api';
import OrgForm from './components/OrgForm';

const OrgList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentOrg, setCurrentOrg] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');
  const [tenantList, setTenantList] = useState<any[]>([]);
  const [domainList, setDomainList] = useState<any[]>([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [tenants, domains] = await Promise.all([
        getTenantListAll(),
        getDomainListAll(),
      ]);
      setTenantList(Array.isArray(tenants) ? tenants : []);
      setDomainList(Array.isArray(domains) ? domains : []);
    } catch (error) {
      console.error('加载数据失败', error);
    }
  };

  const columns: ProColumns<any>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: '组织名称',
      dataIndex: 'orgName',
      key: 'orgName',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
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
      title: '所属域',
      dataIndex: 'domainId',
      key: 'domainId',
      render: (_, record) => {
        const domain = domainList.find(d => d.id === record.domainId);
        return <Tag color="green">{domain?.domainName || '-'}</Tag>;
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
          title="确定删除该组织吗？"
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
    setCurrentOrg(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteOrg(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createOrg(values);
        message.success('创建成功');
      } else {
        await updateOrg({ ...currentOrg, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="组织管理">
      <ProTable
        headerTitle="组织列表"
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
              setCurrentOrg(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建组织
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getOrgPage(params);
          return {
            data: response.records || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <OrgForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentOrg}
        type={modalType}
        tenantList={tenantList}
        domainList={domainList}
      />
    </PageContainer>
  );
};

export default OrgList;
