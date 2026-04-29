import { useEffect } from 'react';
import { Space } from 'antd';
import {
  QuestionCircleOutlined,
} from '@ant-design/icons';
import type { ProSettings } from '@ant-design/pro-components';

export type SiderTheme = 'light' | 'dark';

const GlobalHeaderRight: React.FC = () => {
  const actionClassName = 'action';

  useEffect(() => {
    // 可以在这里添加一些初始化逻辑
  }, []);

  return (
    <Space className="right">
      <span
        className={actionClassName}
        onClick={() => {
          window.open('https://pro.ant.design/docs/getting-started');
        }}
      >
        <QuestionCircleOutlined />
      </span>
    </Space>
  );
};

export default GlobalHeaderRight;
