import axios from 'axios'

const request = axios.create({
  baseURL: '',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 可以在这里添加token等公共请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('请求错误:', error)
    // 处理401未授权等错误
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request
