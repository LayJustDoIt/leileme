import { get, post, put, del } from './request'
import type {
  AdminContentVO,
  AdminContentSaveRequest,
  AdminContentStatusRequest,
  ContentListQuery,
  ContentStatus,
  PageResult
} from '@/types'

const BASE = '/api/admin/v1/contents'

export function listContents(params: ContentListQuery) {
  return get<PageResult<AdminContentVO>>(BASE, params as Record<string, unknown>)
}

export function getContent(id: number) {
  return get<AdminContentVO>(`${BASE}/${id}`)
}

export function createContent(data: AdminContentSaveRequest) {
  return post<AdminContentVO>(BASE, data)
}

export function updateContent(id: number, data: AdminContentSaveRequest) {
  return put<AdminContentVO>(`${BASE}/${id}`, data)
}

export function updateContentStatus(id: number, status: ContentStatus) {
  return put<AdminContentVO>(`${BASE}/${id}/status`, { status } as AdminContentStatusRequest)
}

export function deleteContent(id: number) {
  return del<null>(`${BASE}/${id}`)
}
