import React, { useRef, useState, useEffect } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag } from 'antd';
import { getGroupPage, getTenantListAll, getDomainListAll, getOrgListAll, getDeptListAll, deleteGroup, createGroup, updateGroup } from '@/services/api';
import GroupForm from './components/GroupForm';

const GroupList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentGroup, setCurrentGroup] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');
  const [tenantList, setTenantList] = useState<any[]>([]);
  const [domainList, setDomainList] = useState<any[]>([]);
  const [orgList, setOrgList] = useState<any[]>([]);
  const [deptList, setDeptList] = useState<any[]>([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [tenants, domains, orgs, depts] = await Promise.all([
        getTenantListAll(),
        getDomainListAll(),
        getOrgListAll(),
        getDeptListAll(),
      ]);
      setTenantList(Array.isArray(tenants) ? tenants : []);
      setDomainList(Array.isArray(domains) ? domains : []);
      setOrgList(Array.isArray(orgs) ? orgs : []);
      setDeptList(Array.isArray(depts) ? depts : []);
    } catch (error) {
      console.error('加载数据失败', error);
    }
  };

  const columns: ProColumns<any>[] = [
    {
      title: '组编码',
      dataIndex: 'groupCode',
      key: 'groupCode',
      copyable: true,
    },
    {
      title: '组名称',
      dataIndex: 'groupName',
      key: 'groupName',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
    },
    {
      title: '所属租户',
      dataIndex: 'tenantCode',
      key: 'tenantCode',
      render: (_, record) => {
        const tenant = tenantList.find(t => t.tenantCode === record.tenantCode);
        return <Tag color="blue">{tenant?.tenantName || '-'}</Tag>;
      },
    },
    {
      title: '所属域',
      dataIndex: 'domainCode',
      key: 'domainCode',
      render: (_, record) => {
        const domain = domainList.find(d => d.domainCode === record.domainCode);
        return <Tag color="green">{domain?.domainName || '-'}</Tag>;
      },
    },
    {
      title: '所属组织',
      dataIndex: 'orgCode',
      key: 'orgCode',
      render: (_, record) => {
        const org = orgList.find(o => o.orgCode === record.orgCode);
        return <Tag color="orange">{org?.orgName || '-'}</Tag>;
      },
    },
    {
      title: '所属部门',
      dataIndex: 'deptCode',
      key: 'deptCode',
      render: (_, record) => {
        const dept = deptList.find(d => d.deptCode === record.deptCode);
        return <Tag color="purple">{dept?.deptName || '-'}</Tag>;
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
          title="确定删除该组吗？"
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
    setCurrentGroup(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteGroup(record.groupCode);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createGroup(values);
        message.success('创建成功');
      } else {
        await updateGroup({ ...currentGroup, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="组管理">
      <ProTable
        headerTitle="组列表"
        actionRef={actionRef}
        rowKey="groupCode"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCurrentGroup(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建组
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getGroupPage(params);
          return {
            data: response.records || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <GroupForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentGroup}
        type={modalType}
        tenantList={tenantList}
        domainList={domainList}
        orgList={orgList}
        deptList={deptList}
      />
    </PageContainer>
  );
};

export default GroupList;
