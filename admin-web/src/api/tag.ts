import { get, post, put, del } from './request'
import type {
  AdminTagVO,
  AdminTagSaveRequest,
  TagStatus,
  PageResult,
  PageQuery
} from '@/types'

const BASE = '/api/admin/v1/tags'

export interface TagListQuery extends PageQuery {
  keyword?: string
  status?: TagStatus
}

export function listTags(params: TagListQuery) {
  return get<PageResult<AdminTagVO>>(BASE, params as Record<string, unknown>)
}

export function listAllTags() {
  return get<AdminTagVO[]>(`${BASE}/all`)
}

export function createTag(data: AdminTagSaveRequest) {
  return post<AdminTagVO>(BASE, data)
}

export function updateTag(id: number, data: AdminTagSaveRequest) {
  return put<AdminTagVO>(`${BASE}/${id}`, data)
}

export function deleteTag(id: number) {
  return del<null>(`${BASE}/${id}`)
}
