import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, Radio, Select, TreeSelect, InputNumber } from 'antd';
import { getPermissionList } from '@/services/api';

interface PermissionFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues?: any;
  type: 'create' | 'edit';
}

const PermissionForm: React.FC<PermissionFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  type,
}) => {
  const [form] = Form.useForm();
  const [permissionTree, setPermissionTree] = useState<any[]>([]);
  const [permType, setPermType] = useState<string>(initialValues?.permType || 'MENU');

  useEffect(() => {
    fetchPermissionTree();
  }, []);

  useEffect(() => {
    if (visible && initialValues) {
      form.setFieldsValue(initialValues);
      setPermType(initialValues.permType || 'MENU');
    } else if (visible) {
      form.resetFields();
      form.setFieldsValue({ status: 1, permType: 'MENU', sortOrder: 0 });
      setPermType('MENU');
    }
  }, [visible, initialValues, form]);

  const fetchPermissionTree = async () => {
    try {
      const response: any = await getPermissionList({ pageSize: 999 });
      const list = response.data || [];
      // 构建树形结构
      const tree = buildTree(list);
      setPermissionTree(tree);
    } catch (error) {
      console.error('获取权限列表失败:', error);
    }
  };

  const buildTree = (list: any[]): any[] => {
    const map: Record<string, any> = {};
    const roots: any[] = [];

    list.forEach((item) => {
      map[item.id] = { ...item, children: [] };
    });

    list.forEach((item) => {
      if (item.parentId && map[item.parentId]) {
        map[item.parentId].children.push(map[item.id]);
      } else {
        roots.push(map[item.id]);
      }
    });

    return roots;
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onSubmit(values);
      form.resetFields();
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  const handleTypeChange = (value: string) => {
    setPermType(value);
  };

  return (
    <Modal
      title={type === 'create' ? '新建权限' : '编辑权限'}
      visible={visible}
      onOk={handleSubmit}
      onCancel={handleCancel}
      width={600}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="permCode"
          label="权限编码"
          rules={[
            { required: true, message: '请输入权限编码' },
            { min: 2, message: '权限编码至少2个字符' },
            { max: 100, message: '权限编码最多100个字符' },
          ]}
        >
          <Input placeholder="如: user:create, /api/users" disabled={type === 'edit'} />
        </Form.Item>

        <Form.Item
          name="permName"
          label="权限名称"
          rules={[
            { required: true, message: '请输入权限名称' },
            { max: 50, message: '权限名称最多50个字符' },
          ]}
        >
          <Input placeholder="请输入权限名称" />
        </Form.Item>

        <Form.Item
          name="permType"
          label="权限类型"
          rules={[{ required: true, message: '请选择权限类型' }]}
        >
          <Select placeholder="请选择权限类型" onChange={handleTypeChange}>
            <Select.Option value="MENU">菜单</Select.Option>
            <Select.Option value="BUTTON">按钮</Select.Option>
            <Select.Option value="API">API接口</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="parentId"
          label="上级权限"
        >
          <TreeSelect
            treeData={permissionTree}
            fieldNames={{ label: 'permName', value: 'id', children: 'children' }}
            placeholder="请选择上级权限"
            allowClear
            treeDefaultExpandAll
          />
        </Form.Item>

        {permType === 'MENU' && (
          <Form.Item
            name="path"
            label="路径"
          >
            <Input placeholder="如: /user/list" />
          </Form.Item>
        )}

        <Form.Item
          name="sortOrder"
          label="排序"
        >
          <InputNumber min={0} placeholder="数字越小越靠前" style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="status"
          label="状态"
        >
          <Radio.Group>
            <Radio value={1}>启用</Radio>
            <Radio value={0}>禁用</Radio>
          </Radio.Group>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default PermissionForm;
