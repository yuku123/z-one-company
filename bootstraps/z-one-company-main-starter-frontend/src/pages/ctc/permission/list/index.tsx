import React, { useRef, useState } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, PageContainer } from '@ant-design/pro-components';
import { Button, Popconfirm, message, Tag, Tree } from 'antd';
import { getPermissionList, deletePermission, createPermission, updatePermission } from '@/services/api';
import PermissionForm from './components/PermissionForm';

const PermissionList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentPermission, setCurrentPermission] = useState<any>(null);
  const [modalType, setModalType] = useState<'create' | 'edit'>('create');
  const [viewMode, setViewMode] = useState<'table' | 'tree'>('table');

  const columns: ProColumns<any>[] = [
    {
      title: '权限编码',
      dataIndex: 'permCode',
      key: 'permCode',
      render: (dom, record) => (
        <a onClick={() => handleEdit(record)}>{dom}</a>
      ),
    },
    {
      title: '权限名称',
      dataIndex: 'permName',
      key: 'permName',
    },
    {
      title: '权限类型',
      dataIndex: 'permType',
      key: 'permType',
      render: (_, record) => {
        const typeMap: Record<string, { color: string; text: string }> = {
          MENU: { color: 'blue', text: '菜单' },
          BUTTON: { color: 'green', text: '按钮' },
          API: { color: 'orange', text: 'API' },
        };
        const config = typeMap[record.permType] || { color: 'default', text: record.permType };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '路径',
      dataIndex: 'path',
      key: 'path',
      ellipsis: true,
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      hideInSearch: true,
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
        <a key="addChild" onClick={() => handleAddChild(record)}>
          添加子项
        </a>,
        <Popconfirm
          key="delete"
          title="确定删除该权限吗？"
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
    setCurrentPermission(record);
    setModalType('edit');
    setModalVisible(true);
  };

  const handleAddChild = (record: any) => {
    setCurrentPermission({ parentId: record.id });
    setModalType('create');
    setModalVisible(true);
  };

  const handleDelete = async (record: any) => {
    try {
      await deletePermission(record.id);
      message.success('删除成功');
      actionRef.current?.reload();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleModalSubmit = async (values: any) => {
    try {
      if (modalType === 'create') {
        await createPermission({ ...currentPermission, ...values });
        message.success('创建成功');
      } else {
        await updatePermission(currentPermission.id, { ...currentPermission, ...values });
        message.success('更新成功');
      }
      setModalVisible(false);
      actionRef.current?.reload();
    } catch (error) {
      message.error('操作失败');
    }
  };

  return (
    <PageContainer title="权限管理">
      {viewMode === 'table' ? (
        <ProTable
          headerTitle="权限列表"
          actionRef={actionRef}
          rowKey="id"
          search={{
            labelWidth: 120,
          }}
          toolBarRender={() => [
            <Button
              key="viewMode"
              onClick={() => setViewMode('tree')}
            >
              树形视图
            </Button>,
            <Button
              type="primary"
              key="primary"
              onClick={() => {
                setCurrentPermission(null);
                setModalType('create');
                setModalVisible(true);
              }}
            >
              <PlusOutlined /> 新建权限
            </Button>,
          ]}
          request={async (params) => {
            const response: any = await getPermissionList(params);
            return {
              data: response.data || [],
              success: true,
              total: response.total || 0,
            };
          }}
          columns={columns}
        />
      ) : (
        <div>
          <div style={{ marginBottom: 16 }}>
            <Button onClick={() => setViewMode('table')}>返回列表</Button>
            <Button type="primary" style={{ marginLeft: 8 }}>
              <PlusOutlined /> 新建权限
            </Button>
          </div>
          <Tree
            treeData={[]}
            fieldNames={{ title: 'permName', key: 'id', children: 'children' }}
          />
        </div>
      )}
      <PermissionForm
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleModalSubmit}
        initialValues={currentPermission}
        type={modalType}
      />
    </PageContainer>
  );
};

export default PermissionList;
