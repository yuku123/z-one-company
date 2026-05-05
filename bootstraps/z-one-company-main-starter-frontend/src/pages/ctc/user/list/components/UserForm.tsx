import React from 'react';
import { Modal, Form, Input, Select, Radio } from 'antd';

interface UserFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues?: any;
  type: 'create' | 'edit';
  tenantList?: any[];
  deptList?: any[];
  roleList?: any[];
}

const UserForm: React.FC<UserFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  type,
  tenantList = [],
  deptList = [],
  roleList = [],
}) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (visible) {
      if (type === 'edit' && initialValues) {
        form.setFieldsValue({
          ...initialValues,
          // userName is read-only in edit, handled in fields
        });
      } else {
        form.resetFields();
      }
    }
  }, [visible, initialValues, type]);

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
      title={type === 'create' ? '新建用户' : '编辑用户'}
      open={visible}
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
          name="userName"
          label="用户名"
          rules={[
            { required: true, message: '请输入用户名' },
            { min: 3, message: '用户名至少3个字符' },
            { max: 20, message: '用户名最多20个字符' },
          ]}
        >
          <Input placeholder="请输入用户名" disabled={type === 'edit'} />
        </Form.Item>

        {type === 'create' && (
          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少6个字符' },
            ]}
          >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
        )}

        <Form.Item
          name="realName"
          label="真实姓名"
        >
          <Input placeholder="请输入真实姓名" />
        </Form.Item>

        <Form.Item
          name="email"
          label="邮箱"
          rules={[
            { type: 'email', message: '请输入有效的邮箱地址' },
          ]}
        >
          <Input placeholder="请输入邮箱" />
        </Form.Item>

        <Form.Item
          name="phone"
          label="手机号"
        >
          <Input placeholder="请输入手机号" />
        </Form.Item>

        <Form.Item
          name="tenantCode"
          label="租户"
        >
          <Select
            placeholder="请选择租户"
            allowClear
            showSearch
            optionFilterProp="label"
          >
            {(tenantList || []).map(t => (
              <Select.Option key={t.tenantCode} value={t.tenantCode} label={t.tenantName}>
                {t.tenantName} ({t.tenantCode})
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name="deptId"
          label="部门"
        >
          <Select
            placeholder="请选择部门"
            allowClear
            showSearch
            optionFilterProp="label"
          >
            {(deptList || []).map(d => (
              <Select.Option key={d.id} value={d.id} label={d.deptName}>
                {d.deptName}
              </Select.Option>
            ))}
          </Select>
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

export default UserForm;
