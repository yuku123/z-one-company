import axios from 'axios'

// 通用请求实例 - 用于 /api 开头的接口
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 认证请求实例 - 用于 /auth 开头的接口（绕过 /api 前缀）
const authRequest = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
const setupInterceptors = (instance: axios.AxiosInstance) => {
  instance.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  instance.interceptors.response.use(
    response => {
      const data = response.data;
      if (data && typeof data === 'object' && 'code' in data && 'data' in data) {
        if (data.code !== 200) {
          return Promise.reject(new Error(data.message || '请求失败'));
        }
        return data.data;
      }
      return data;
    },
    error => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        window.location.href = '/login';
      } else if (error.response?.status === 403) {
        console.error('没有权限访问该资源');
      } else if (error.response?.status === 500) {
        console.error('服务器内部错误');
      }
      return Promise.reject(error);
    }
  );
}

setupInterceptors(request)
setupInterceptors(authRequest)

export { authRequest }
export default request
