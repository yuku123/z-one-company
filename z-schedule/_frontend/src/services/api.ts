import axios, { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ApiResponse } from '@/types';
import { message } from 'antd';

// 创建axios实例
const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('z-schedule-token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    const { data } = response;

    // 如果code不是200，说明业务错误
    if (data.code !== 200) {
      message.error(data.msg || '操作失败');
      return Promise.reject(new Error(data.msg));
    }

    return response;
  },
  (error) => {
    // 处理HTTP错误
    if (error.response) {
      const status = error.response.status;
      switch (status) {
        case 401:
          message.error('未授权，请重新登录');
          // 可以在这里处理登出逻辑
          break;
        case 403:
          message.error('拒绝访问');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error('服务器内部错误');
          break;
        default:
          message.error(`请求失败: ${status}`);
      }
    } else if (error.request) {
      message.error('网络错误，请检查网络连接');
    } else {
      message.error(error.message || '请求失败');
    }

    return Promise.reject(error);
  }
);

// 封装请求方法
export const http = {
  get: <T>(url: string, params?: object): Promise<T> => {
    return api.get(url, { params }).then((res) => res.data.content);
  },

  post: <T>(url: string, data?: object): Promise<T> => {
    return api.post(url, data).then((res) => res.data.content);
  },

  put: <T>(url: string, data?: object): Promise<T> => {
    return api.put(url, data).then((res) => res.data.content);
  },

  delete: <T>(url: string): Promise<T> => {
    return api.delete(url).then((res) => res.data.content);
  },
};

export default api;
