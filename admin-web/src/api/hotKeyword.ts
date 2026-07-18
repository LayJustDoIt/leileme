import { get, post, put, del } from './request'
import type {
  AdminHotKeywordVO,
  AdminHotKeywordSaveRequest,
  HotKeywordStatus,
  PageResult,
  PageQuery
} from '@/types'

const BASE = '/api/admin/v1/hot-keywords'

export interface HotKeywordListQuery extends PageQuery {
  keyword?: string
  status?: HotKeywordStatus
}

export function listHotKeywords(params: HotKeywordListQuery) {
  return get<PageResult<AdminHotKeywordVO>>(BASE, params as Record<string, unknown>)
}

export function createHotKeyword(data: AdminHotKeywordSaveRequest) {
  return post<AdminHotKeywordVO>(BASE, data)
}

export function updateHotKeyword(id: number, data: AdminHotKeywordSaveRequest) {
  return put<AdminHotKeywordVO>(`${BASE}/${id}`, data)
}

export function deleteHotKeyword(id: number) {
  return del<null>(`${BASE}/${id}`)
}
