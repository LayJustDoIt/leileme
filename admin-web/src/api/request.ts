import axios, { AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

const TOKEN_KEY = 'leileme_admin_token'
const INFO_KEY = 'leileme_admin_info'

const service: AxiosInstance = axios.create({
  baseURL: '',
  timeout: 15000
})

// 请求拦截器：自动添加 Authorization
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理业务码与异常
service.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse<unknown>
    // 非标准结构（如二进制）直接返回
    if (res === null || typeof res !== 'object' || typeof res.code === 'undefined') {
      return response.data
    }
    if (res.code === 0) {
      return res.data
    }
    // 业务错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const status = error.response?.status
    const respData = error.response?.data
    if (status === 401) {
      // 清除登录信息并跳转登录
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(INFO_KEY)
      ElMessage.error('登录已过期，请重新登录')
      // 避免重复跳转
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
    const msg = respData?.message
    if (msg) {
      ElMessage.error(msg)
    } else if (error.message === 'Network Error') {
      ElMessage.error('网络异常')
    } else if (error.message?.includes('timeout')) {
      ElMessage.error('请求超时')
    } else {
      ElMessage.error('网络异常')
    }
    return Promise.reject(error)
  }
)

// 导出便捷方法
export function get<T = unknown>(url: string, params?: Record<string, unknown>): Promise<T> {
  return service.get(url, { params }) as unknown as Promise<T>
}

export function post<T = unknown>(url: string, data?: unknown): Promise<T> {
  return service.post(url, data) as unknown as Promise<T>
}

export function put<T = unknown>(url: string, data?: unknown): Promise<T> {
  return service.put(url, data) as unknown as Promise<T>
}

export function del<T = unknown>(url: string, params?: Record<string, unknown>): Promise<T> {
  return service.delete(url, { params }) as unknown as Promise<T>
}

export default service
