import { get, put } from './request'
import type { PublicConfigVO, PublicConfigSaveRequest } from '@/types'

const BASE = '/api/admin/v1/public-config'

export function getPublicConfig() {
  return get<PublicConfigVO>(BASE)
}

export function updatePublicConfig(data: PublicConfigSaveRequest) {
  return put<PublicConfigVO>(BASE, data)
}
