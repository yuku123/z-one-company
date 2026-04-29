/**
 * 统一请求工具，自动处理 Token
 */

const API_BASE_URL = ''

// 从 cookie 中获取 token
function getTokenFromCookie() {
  const cookies = document.cookie.split(';')
  for (let cookie of cookies) {
    const [name, value] = cookie.trim().split('=')
    if (name === 'token') {
      return value
    }
  }
  return null
}

// 设置 token 到 cookie
export function setToken(token) {
  document.cookie = `token=${token}; path=/; max-age=86400`
}

// 清除 token
export function clearToken() {
  document.cookie = 'token=; path=/; max-age=0'
}

// 封装 fetch 请求
export async function request(url, options = {}) {
  const token = getTokenFromCookie()

  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
      ...options.headers,
    },
  }

  // 合并配置
  const finalOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...(options.headers || {}),
    },
  }

  const response = await fetch(`${API_BASE_URL}${url}`, finalOptions)

  // 处理 401 未授权
  if (response.status === 401) {
    clearToken()
    window.location.href = '/login'
    throw new Error('Unauthorized')
  }

  return response
}

// 快捷方法
export const get = (url, options = {}) => request(url, { ...options, method: 'GET' })
export const post = (url, data, options = {}) => request(url, { ...options, method: 'POST', body: JSON.stringify(data) })
export const put = (url, data, options = {}) => request(url, { ...options, method: 'PUT', body: JSON.stringify(data) })
export const del = (url, options = {}) => request(url, { ...options, method: 'DELETE' })
