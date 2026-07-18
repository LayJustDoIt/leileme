import { get, post } from './request'
import type { AdminLoginRequest, AdminLoginResponse, ChangePasswordRequest, AdminInfo } from '@/types'

const BASE = '/api/admin/v1/auth'

export function login(data: AdminLoginRequest) {
  return post<AdminLoginResponse>(`${BASE}/login`, data)
}

export function getAdminInfo() {
  return get<AdminInfo>(`${BASE}/me`)
}

export function changePassword(data: ChangePasswordRequest) {
  return post<null>(`${BASE}/change-password`, data)
}
