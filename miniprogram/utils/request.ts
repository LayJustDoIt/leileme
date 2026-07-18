import { env } from '../config/env';
import { getToken, clearAuth } from './auth';

// 后端统一响应结构
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  requestId?: string;
}

export interface RequestOptions {
  url: string;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  data?: Record<string, any>;
  header?: Record<string, string>;
  showLoading?: boolean;
  loadingText?: string;
  showErrorToast?: boolean;
  timeout?: number;
  // 是否跳过自动注入 Authorization（登录接口本身不需要）
  skipAuth?: boolean;
}

// 业务错误（code != 0）
export class BizError extends Error {
  code: number;
  requestId?: string;
  constructor(message: string, code: number, requestId?: string) {
    super(message);
    this.name = 'BizError';
    this.code = code;
    this.requestId = requestId;
  }
}

// 网络错误（wx.request fail）
export class NetError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'NetError';
  }
}

// 未登录错误（HTTP 401）
export class AuthError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuthError';
  }
}

let loadingCounter = 0;

function showLoadingIfNeed(text: string) {
  if (loadingCounter === 0) {
    wx.showLoading({ title: text, mask: true });
  }
  loadingCounter++;
}

function hideLoadingIfNeed() {
  loadingCounter = Math.max(0, loadingCounter - 1);
  if (loadingCounter === 0) {
    wx.hideLoading();
  }
}

export function request<T = any>(opts: RequestOptions): Promise<T> {
  const {
    url,
    method = 'GET',
    data,
    header = {},
    showLoading = false,
    loadingText = '加载中',
    showErrorToast = true,
    timeout,
    skipAuth = false,
  } = opts;

  const fullUrl = /^https?:\/\//.test(url) ? url : `${env.apiBase}${url}`;
  const finalHeader: Record<string, string> = {
    'Content-Type': 'application/json',
    ...header,
  };

  // 自动注入 Authorization（登录接口本身跳过）
  if (!skipAuth) {
    const token = getToken();
    if (token) {
      finalHeader['Authorization'] = `Bearer ${token}`;
    }
  }

  if (showLoading) showLoadingIfNeed(loadingText);

  return new Promise<T>((resolve, reject) => {
    wx.request({
      url: fullUrl,
      method,
      data,
      header: finalHeader,
      timeout: timeout || env.requestTimeout,
      success(res) {
        const statusCode = res.statusCode;
        const body = res.data as ApiResponse<T>;

        // 401：清除失效 Token，不自动重试
        if (statusCode === 401) {
          clearAuth();
          const msg = (body && body.message) || '登录已过期，请重新登录';
          if (showErrorToast) wx.showToast({ title: msg, icon: 'none' });
          reject(new AuthError(msg));
          return;
        }

        if (statusCode < 200 || statusCode >= 300) {
          const msg = `网络异常（${statusCode}）`;
          if (showErrorToast) wx.showToast({ title: msg, icon: 'none' });
          reject(new NetError(msg));
          return;
        }

        if (body && typeof body === 'object' && 'code' in body) {
          if (body.code === 0) {
            resolve(body.data);
          } else {
            if (showErrorToast) wx.showToast({ title: body.message || '请求失败', icon: 'none' });
            reject(new BizError(body.message || '请求失败', body.code, body.requestId));
          }
        } else {
          resolve(body as unknown as T);
        }
      },
      fail(err) {
        const msg = err.errMsg || '网络请求失败';
        if (showErrorToast) wx.showToast({ title: '网络异常，请稍后再试', icon: 'none' });
        reject(new NetError(msg));
      },
      complete() {
        if (showLoading) hideLoadingIfNeed();
      },
    });
  });
}

// 便捷方法
export function get<T = any>(url: string, data?: Record<string, any>, opts?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'GET', data, ...opts });
}

export function post<T = any>(url: string, data?: Record<string, any>, opts?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'POST', data, ...opts });
}

export function del<T = any>(url: string, data?: Record<string, any>, opts?: Partial<RequestOptions>) {
  return request<T>({ url, method: 'DELETE', data, ...opts });
}
