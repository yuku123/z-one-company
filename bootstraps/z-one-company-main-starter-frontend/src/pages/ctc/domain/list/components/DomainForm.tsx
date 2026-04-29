import React from 'react';
import { Modal, Form, Input, Select } from 'antd';

interface DomainFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues?: any;
  type: 'create' | 'edit';
  tenantList: any[];
}

const DomainForm: React.FC<DomainFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  type,
  tenantList,
}) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (visible) {
      if (type === 'edit' && initialValues) {
        form.setFieldsValue(initialValues);
      } else {
        form.resetFields();
      }
    }
  }, [visible, initialValues, type]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onSubmit(values);
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Modal
      title={type === 'create' ? '新建域' : '编辑域'}
      open={visible}
      onOk={handleOk}
      onCancel={onCancel}
      width={500}
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          status: 1,
        }}
      >
        <Form.Item
          name="domainCode"
          label="域编码"
          rules={[
            { required: true, message: '请输入域编码' },
            { max: 50, message: '域编码不能超过50个字符' },
          ]}
        >
          <Input placeholder="请输入域编码" disabled={type === 'edit'} />
        </Form.Item>
        <Form.Item
          name="domainName"
          label="域名称"
          rules={[
            { required: true, message: '请输入域名称' },
            { max: 100, message: '域名称不能超过100个字符' },
          ]}
        >
          <Input placeholder="请输入域名称" />
        </Form.Item>
        <Form.Item
          name="tenantId"
          label="所属租户"
          rules={[{ required: true, message: '请选择所属租户' }]}
        >
          <Select placeholder="请选择所属租户">
            {(tenantList || []).map((tenant: any) => (
              <Select.Option key={tenant.id} value={tenant.id}>
                {tenant.tenantName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="description"
          label="描述"
          rules={[{ max: 500, message: '描述不能超过500个字符' }]}
        >
          <Input.TextArea rows={3} placeholder="请输入描述" />
        </Form.Item>
        <Form.Item
          name="status"
          label="状态"
          rules={[{ required: true, message: '请选择状态' }]}
        >
          <Select placeholder="请选择状态">
            <Select.Option value={1}>启用</Select.Option>
            <Select.Option value={0}>禁用</Select.Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default DomainForm;
