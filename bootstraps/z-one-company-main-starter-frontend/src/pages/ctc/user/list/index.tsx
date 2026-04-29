import React, { useRef, useState } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Switch, Popconfirm, Tag, message } from 'antd';
import { getUserList, deleteUser, createUser, updateUser } from '@/services/api';
import UserForm from './components/UserForm';

const UserList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentUser, setCurrentUser] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');

  const columns: ProColumns<any>[] = [
    {
      title: '用户名',
      dataIndex: 'userName',
      key: 'userName',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
    },
    {
      title: '真实姓名',
      dataIndex: 'realName',
      key: 'realName',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      copyable: true,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      key: 'phone',
      copyable: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (_, record) => (
        <Switch
          checked={record.status === 1}
          onChange={(checked) => handleStatusChange(record, checked)}
        />
      ),
    },
    {
      title: '角色',
      dataIndex: 'roles',
      key: 'roles',
      render: (_, record) => (
        <span>
          {record.roles?.map((role: string) => (
            <Tag key={role} color="blue">
              {role}
            </Tag>
          ))}
        </span>
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
        <Popconfirm
          key="delete"
          title="确定删除该用户吗？"
          onConfirm={() => handleDelete(record)}
          okText="确定"
          cancelText="取消"
        >
          <a style={{ color: '#ff4d4f' }}>删除</a>
        </Popconfirm>,
      ],
    },
  ];

  const handleStatusChange = async (record: any, checked: boolean) => {
    try {
      await updateUser(record.id, { ...record, status: checked ? 1 : 0 });
      message.success('状态更新成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('状态更新失败');
    }
  };

  const handleEdit = (record: any) => {
    setCurrentUser(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deleteUser(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createUser(values);
        message.success('创建成功');
      } else {
        await updateUser(currentUser.id, { ...currentUser, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="用户管理">
      <ProTable
        headerTitle="用户列表"
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
              setCurrentUser(null);
              setModalType('create');
              setModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建用户
          </Button>,
        ]}
        request={async (params) => {
          const response: any = await getUserList(params);
          return {
            data: response.data || [],
            success: true,
            total: response.total || 0,
          };
        }}
        columns={columns}
      />
      <UserForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentUser}
        type={modalType}
      />
    </PageContainer>
  );
};

export default UserList;
