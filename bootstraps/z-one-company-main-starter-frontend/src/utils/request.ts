import { request as umiRequest } from '@umijs/max';
import { message } from 'antd';
import Cookies from 'js-cookie';

// 请求拦截器
const request = async <T = any>(url: string, options: any = {}): Promise<T> => {
  // 从 Cookie 获取 token
  const token = Cookies.get('token');

  // 设置请求头
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await umiRequest(url, {
      ...options,
      headers,
      errorConfig: {
        errorHandler: (error: any) => {
          const { response } = error;

          if (response?.status === 401) {
            // 未授权，清除 token 并跳转到登录页
            Cookies.remove('token');
            window.location.href = '/login';
            message.error('登录已过期，请重新登录');
          } else if (response?.status === 403) {
            message.error('没有权限访问该资源');
          } else if (response?.status === 500) {
            message.error('服务器内部错误');
          } else {
            message.error(response?.data?.message || '请求失败');
          }

          throw error;
        },
      },
    });

    return response;
  } catch (error) {
    throw error;
  }
};

export default request;
