// ============================================================
// 管理后台统一枚举配置
// 数据库/接口值保持英文代码，前端显示统一中文。
// 所有页面必须从这里引入，不允许重复写映射。
// 未知值回退显示原始代码，避免页面空白。
// ============================================================

// ---------- 内容类型 ----------
export const CONTENT_TYPE_OPTIONS = [
  { value: 'HOT_NEWS', label: '热点资讯' },
  { value: 'COPYWRITING', label: '文案脚本' },
  { value: 'TOOL', label: '工具资源' },
  { value: 'GUIDE', label: '方法教程' },
  { value: 'RANDOM_TIP', label: '随机建议' }
] as const

export const CONTENT_TYPE_LABELS: Record<string, string> = Object.fromEntries(
  CONTENT_TYPE_OPTIONS.map((item) => [item.value, item.label])
)

// ---------- 内容状态：0=草稿 1=发布 2=下架 ----------
export const CONTENT_STATUS_OPTIONS = [
  { value: 0, label: '草稿', tagType: 'info' as const },
  { value: 1, label: '已发布', tagType: 'success' as const },
  { value: 2, label: '已下架', tagType: 'danger' as const }
] as const

export const CONTENT_STATUS_LABELS: Record<number, string> = Object.fromEntries(
  CONTENT_STATUS_OPTIONS.map((item) => [item.value, item.label])
)

export const CONTENT_STATUS_TAG_TYPES: Record<number, 'info' | 'success' | 'danger'> = Object.fromEntries(
  CONTENT_STATUS_OPTIONS.map((item) => [item.value, item.tagType])
)

// ---------- 启用状态：1=启用 0=禁用（分类/标签/热词/推荐位）----------
export const ENABLED_STATUS_OPTIONS = [
  { value: 1, label: '启用', tagType: 'success' as const },
  { value: 0, label: '停用', tagType: 'info' as const }
] as const

export const ENABLED_STATUS_LABELS: Record<number, string> = Object.fromEntries(
  ENABLED_STATUS_OPTIONS.map((item) => [item.value, item.label])
)

export const ENABLED_STATUS_TAG_TYPES: Record<number, 'success' | 'info'> = Object.fromEntries(
  ENABLED_STATUS_OPTIONS.map((item) => [item.value, item.tagType])
)

// ---------- 推荐位类型（slot_code）----------
export const RECOMMENDATION_SLOT_OPTIONS = [
  { value: 'TODAY_RECOMMEND', label: '今日推荐' },
  { value: 'GUESS_YOU_LIKE', label: '猜你喜欢' },
  { value: 'SCENE_ENTRY', label: '场景入口' },
  { value: 'AUDIENCE_ENTRY', label: '人群入口' }
] as const

export const RECOMMENDATION_SLOT_LABELS: Record<string, string> = Object.fromEntries(
  RECOMMENDATION_SLOT_OPTIONS.map((item) => [item.value, item.label])
)

// ---------- 反馈状态 ----------
export const FEEDBACK_STATUS_OPTIONS = [
  { value: 'PENDING', label: '待处理', tagType: 'warning' as const },
  { value: 'PROCESSED', label: '已处理', tagType: 'success' as const },
  { value: 'IGNORED', label: '已忽略', tagType: 'info' as const }
] as const

export const FEEDBACK_STATUS_LABELS: Record<string, string> = Object.fromEntries(
  FEEDBACK_STATUS_OPTIONS.map((item) => [item.value, item.label])
)

export const FEEDBACK_STATUS_TAG_TYPES: Record<string, 'warning' | 'success' | 'info'> = Object.fromEntries(
  FEEDBACK_STATUS_OPTIONS.map((item) => [item.value, item.tagType])
)

// ---------- 管理员状态：1=正常 0=禁用 ----------
export const ADMIN_STATUS_OPTIONS = [
  { value: 1, label: '正常', tagType: 'success' as const },
  { value: 0, label: '禁用', tagType: 'info' as const }
] as const

export const ADMIN_STATUS_LABELS: Record<number, string> = Object.fromEntries(
  ADMIN_STATUS_OPTIONS.map((item) => [item.value, item.label])
)

// ---------- 工具函数：获取 label，未知值回退原始 code ----------
export function getContentTypeLabel(code?: string | null): string {
  if (code == null || code === '') return ''
  return CONTENT_TYPE_LABELS[code] ?? code
}

export function getContentStatusLabel(status?: number | null): string {
  if (status == null) return ''
  return CONTENT_STATUS_LABELS[status] ?? String(status)
}

export function getContentStatusTagType(status?: number | null): 'info' | 'success' | 'danger' {
  if (status == null) return 'info'
  return CONTENT_STATUS_TAG_TYPES[status] ?? 'info'
}

export function getEnabledStatusLabel(status?: number | null): string {
  if (status == null) return ''
  return ENABLED_STATUS_LABELS[status] ?? String(status)
}

export function getEnabledStatusTagType(status?: number | null): 'success' | 'info' {
  if (status == null) return 'info'
  return ENABLED_STATUS_TAG_TYPES[status] ?? 'info'
}

export function getRecommendationSlotLabel(code?: string | null): string {
  if (code == null || code === '') return ''
  return RECOMMENDATION_SLOT_LABELS[code] ?? code
}

export function getFeedbackStatusLabel(status?: string | null): string {
  if (status == null || status === '') return ''
  return FEEDBACK_STATUS_LABELS[status] ?? status
}

export function getFeedbackStatusTagType(status?: string | null): 'warning' | 'success' | 'info' {
  if (status == null) return 'info'
  return FEEDBACK_STATUS_TAG_TYPES[status] ?? 'info'
}

export function getAdminStatusLabel(status?: number | null): string {
  if (status == null) return ''
  return ADMIN_STATUS_LABELS[status] ?? String(status)
}
