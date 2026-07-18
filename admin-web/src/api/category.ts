import { get, post, put, del } from './request'
import type {
  AdminCategoryVO,
  AdminCategorySaveRequest,
  CategoryStatus,
  PageResult,
  PageQuery
} from '@/types'

const BASE = '/api/admin/v1/categories'

export interface CategoryListQuery extends PageQuery {
  keyword?: string
  status?: CategoryStatus
}

export function listCategories(params: CategoryListQuery) {
  return get<PageResult<AdminCategoryVO>>(BASE, params as Record<string, unknown>)
}

export function listAllCategories() {
  return get<AdminCategoryVO[]>(`${BASE}/all`)
}

export function getCategory(id: number) {
  return get<AdminCategoryVO>(`${BASE}/${id}`)
}

export function createCategory(data: AdminCategorySaveRequest) {
  return post<AdminCategoryVO>(BASE, data)
}

export function updateCategory(id: number, data: AdminCategorySaveRequest) {
  return put<AdminCategoryVO>(`${BASE}/${id}`, data)
}

export function deleteCategory(id: number) {
  return del<null>(`${BASE}/${id}`)
}
