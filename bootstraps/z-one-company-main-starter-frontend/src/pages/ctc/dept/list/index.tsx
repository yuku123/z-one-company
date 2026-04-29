import React, { useRef, useState, useEffect } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag } from 'antd';
import { getDeptPage, getTenantListAll, getDomainListAll, getOrgListAll, deleteDept, createDept, updateDept } from '@/services/api';
import DeptForm from './components/DeptForm';

const DeptList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentDept, setCurrentDept] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');
  const [tenantList, setTenantList] = useState<any[]>([]);
  const [domainList, setDomainList] = useState<any[]>([]);
  const [orgList, setOrgList] = useState<any[]>([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [tenants, domains, orgs] = await Promise.all([
        getTenantListAll(),
        getDomainListAll(),
        getOrgListAll(),
      ]);
      setTenantList(Array.isArray(tenants) ? tenants : []);
      setDomainList(Array.isArray(domains) ? domains : []);
      setOrgList(Array.isArray(orgs) ? orgs : []);
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
      title: '部门名称',
      dataIndex: 'deptName',
      key: 'deptName',
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
      title: '所属组织',
      dataIndex: 'orgId',
      key: 'orgId',
      render: (_, record) => {
        const org = orgList.find(o => o.id === record.orgId);
        return <Tag color="orange">{org?.orgName || '-'}</Tag>;
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
          title="确定删除该部门吗？"
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
    setCurrentDept(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteDept(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createDept(values);
        message.success('创建成功');
      } else {
        await updateDept({ ...currentDept, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="部门管理">
      <ProTable
        headerTitle="部门列表"
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
              setCurrentDept(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建部门
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getDeptPage(params);
          return {
            data: response.records || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <DeptForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentDept}
        type={modalType}
        tenantList={tenantList}
        domainList={domainList}
        orgList={orgList}
      />
    </PageContainer>
  );
};

export default DeptList;
