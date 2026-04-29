import React, { useRef, useState } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag } from 'antd';
import { getRoleList, deleteRole, createRole, updateRole } from '@/services/api';
import RoleForm from './components/RoleForm';

const RoleList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRole, setCurrentRole] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');

  const columns: ProColumns<any>[] = [
    {
      title: '角色编码',
      dataIndex: 'roleCode',
      key: 'roleCode',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      key: 'roleName',
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
      title: '所属租户',
      dataIndex: 'tenantCode',
      key: 'tenantCode',
      render: (_, record) => (
        <Tag color="blue">{record.tenantCode}</Tag>
      ),
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
        <a key="permission" onClick={() => handlePermission(record)}>
          权限
        </a>,
        <Popconfirm
          key="delete"
          title="确定删除该角色吗？"
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
    setCurrentRole(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handlePermission = (record: any) => {
    // TODO: 打开权限配置抽屉
    message.info(`配置角色权限: ${record.roleName}`);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteRole(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createRole(values);
        message.success('创建成功');
      } else {
        await updateRole(currentRole.id, { ...currentRole, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="角色管理">
      <ProTable
        headerTitle="角色列表"
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
              setCurrentRole(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建角色
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getRoleList(params);
          return {
            data: response.data || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <RoleForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentRole}
        type={modalType}
      />
    </PageContainer>
  );
};

export default RoleList;
