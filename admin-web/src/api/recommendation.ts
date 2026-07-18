import { get, post, put, del } from './request'
import type {
  AdminRecommendationVO,
  AdminRecommendationSaveRequest,
  RecommendationStatus,
  PageResult,
  PageQuery
} from '@/types'

const BASE = '/api/admin/v1/recommendations'

export interface RecommendationListQuery extends PageQuery {
  slotCode?: string
  status?: RecommendationStatus
}

export function listRecommendations(params: RecommendationListQuery) {
  return get<PageResult<AdminRecommendationVO>>(BASE, params as Record<string, unknown>)
}

export function getRecommendation(id: number) {
  return get<AdminRecommendationVO>(`${BASE}/${id}`)
}

export function createRecommendation(data: AdminRecommendationSaveRequest) {
  return post<AdminRecommendationVO>(BASE, data)
}

export function updateRecommendation(id: number, data: AdminRecommendationSaveRequest) {
  return put<AdminRecommendationVO>(`${BASE}/${id}`, data)
}

export function toggleRecommendationStatus(id: number, status: RecommendationStatus) {
  return put<null>(`${BASE}/${id}/status`, { status })
}

export function deleteRecommendation(id: number) {
  return del<null>(`${BASE}/${id}`)
}
