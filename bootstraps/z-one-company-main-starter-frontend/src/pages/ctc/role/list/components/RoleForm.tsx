import React from 'react';
import { Modal, Form, Input, Radio } from 'antd';

interface RoleFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues?: any;
  type: 'create' | 'edit';
}

const RoleForm: React.FC<RoleFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  type,
}) => {
  const [form] = Form.useForm();

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

  return (
    <Modal
      title={type === 'create' ? '新建角色' : '编辑角色'}
      visible={visible}
      onOk={handleSubmit}
      onCancel={handleCancel}
      width={600}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          status: 1,
          ...initialValues,
        }}
      >
        <Form.Item
          name="roleCode"
          label="角色编码"
          rules={[
            { required: true, message: '请输入角色编码' },
            { min: 2, message: '角色编码至少2个字符' },
            { max: 50, message: '角色编码最多50个字符' },
          ]}
        >
          <Input placeholder="请输入角色编码" disabled={type === 'edit'} />
        </Form.Item>

        <Form.Item
          name="roleName"
          label="角色名称"
          rules={[
            { required: true, message: '请输入角色名称' },
            { max: 50, message: '角色名称最多50个字符' },
          ]}
        >
          <Input placeholder="请输入角色名称" />
        </Form.Item>

        <Form.Item
          name="description"
          label="描述"
        >
          <Input.TextArea placeholder="请输入角色描述" rows={3} />
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

export default RoleForm;
