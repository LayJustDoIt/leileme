import { get } from './request'
import type { DashboardSummaryVO } from '@/types'

const BASE = '/api/admin/v1/dashboard'

export function getDashboardSummary() {
  return get<DashboardSummaryVO>(`${BASE}/summary`)
}
