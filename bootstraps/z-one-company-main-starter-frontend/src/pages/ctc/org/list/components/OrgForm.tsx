import React from 'react';
import { Modal, Form, Input, Select } from 'antd';

interface OrgFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues?: any;
  type: 'create' | 'edit';
  tenantList: any[];
  domainList: any[];
}

const OrgForm: React.FC<OrgFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  type,
  tenantList,
  domainList,
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
      title={type === 'create' ? '新建组织' : '编辑组织'}
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
          name="orgName"
          label="组织名称"
          rules={[
            { required: true, message: '请输入组织名称' },
            { max: 100, message: '组织名称不能超过100个字符' },
          ]}
        >
          <Input placeholder="请输入组织名称" />
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
          name="domainId"
          label="所属域"
          rules={[{ required: true, message: '请选择所属域' }]}
        >
          <Select placeholder="请选择所属域">
            {(domainList || []).map((domain: any) => (
              <Select.Option key={domain.id} value={domain.id}>
                {domain.domainName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="parentId"
          label="上级组织"
        >
          <Select placeholder="请选择上级组织" allowClear>
            <Select.Option value={0}>无</Select.Option>
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

export default OrgForm;
