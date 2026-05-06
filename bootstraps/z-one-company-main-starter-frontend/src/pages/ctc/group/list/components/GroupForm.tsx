import React from 'react';
import { Modal, Form, Input, Select } from 'antd';

interface GroupFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues?: any;
  type: 'create' | 'edit';
  tenantList: any[];
  domainList: any[];
  orgList: any[];
  deptList: any[];
}

const GroupForm: React.FC<GroupFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues,
  type,
  tenantList,
  domainList,
  orgList,
  deptList,
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
      title={type === 'create' ? '新建组' : '编辑组'}
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
          name="groupName"
          label="组名称"
          rules={[
            { required: true, message: '请输入组名称' },
            { max: 100, message: '组名称不能超过100个字符' },
          ]}
        >
          <Input placeholder="请输入组名称" />
        </Form.Item>
        <Form.Item
          name="tenantCode"
          label="所属租户"
          rules={[{ required: true, message: '请选择所属租户' }]}
        >
          <Select placeholder="请选择所属租户">
            {(tenantList || []).map((tenant: any) => (
              <Select.Option key={tenant.tenantCode} value={tenant.tenantCode}>
                {tenant.tenantName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="domainCode"
          label="所属域"
          rules={[{ required: true, message: '请选择所属域' }]}
        >
          <Select placeholder="请选择所属域">
            {(domainList || []).map((domain: any) => (
              <Select.Option key={domain.domainCode} value={domain.domainCode}>
                {domain.domainName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="orgCode"
          label="所属组织"
          rules={[{ required: true, message: '请选择所属组织' }]}
        >
          <Select placeholder="请选择所属组织">
            {(orgList || []).map((org: any) => (
              <Select.Option key={org.orgCode} value={org.orgCode}>
                {org.orgName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          name="deptCode"
          label="所属部门"
          rules={[{ required: true, message: '请选择所属部门' }]}
        >
          <Select placeholder="请选择所属部门">
            {(deptList || []).map((dept: any) => (
              <Select.Option key={dept.deptCode} value={dept.deptCode}>
                {dept.deptName}
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

export default GroupForm;
