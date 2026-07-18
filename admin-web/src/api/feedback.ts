import { get, put } from './request'
import type {
  AdminFeedbackVO,
  AdminFeedbackStatusRequest,
  AdminFeedbackNoteRequest,
  FeedbackStatus,
  FeedbackListQuery,
  PageResult
} from '@/types'

const BASE = '/api/admin/v1/feedbacks'

export function listFeedbacks(params: FeedbackListQuery) {
  return get<PageResult<AdminFeedbackVO>>(BASE, params as Record<string, unknown>)
}

export function getFeedback(id: number) {
  return get<AdminFeedbackVO>(`${BASE}/${id}`)
}

export function updateFeedbackStatus(id: number, status: FeedbackStatus) {
  return put<AdminFeedbackVO>(`${BASE}/${id}/status`, { status } as AdminFeedbackStatusRequest)
}

export function updateFeedbackNote(id: number, adminNote: string) {
  return put<AdminFeedbackVO>(`${BASE}/${id}/note`, { adminNote } as AdminFeedbackNoteRequest)
}
