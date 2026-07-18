// 统一响应结构
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  requestId?: string
}

// 分页结果
export interface PageResult<T> {
  page: number
  size: number
  total: number
  hasNext: boolean
  list: T[]
}

// 分页查询基础参数
export interface PageQuery {
  page?: number
  size?: number
}

// ============== Auth ==============
export interface AdminInfo {
  id: number
  username: string
  displayName: string
}

export interface AdminLoginResponse {
  accessToken: string
  expiresIn: number
  admin: AdminInfo
}

export interface AdminLoginRequest {
  username: string
  password: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

// ============== Content ==============
// 内容类型枚举（与数据库实际取值一致：HOT_NEWS/COPYWRITING/TOOL/GUIDE/RANDOM_TIP）
export type ContentType = 'HOT_NEWS' | 'COPYWRITING' | 'TOOL' | 'GUIDE' | 'RANDOM_TIP'

// 内容状态枚举：0=草稿 1=发布 2=下架
export type ContentStatus = 0 | 1 | 2

export interface AdminContentVO {
  id: number
  title: string
  subtitle?: string
  summary?: string
  body?: string
  searchKeywords?: string
  coverUrl?: string
  sourceName?: string
  sourceUrl?: string
  authorName?: string
  contentType: ContentType
  categoryId?: number
  status: ContentStatus
  isOriginal?: number
  isTop?: number
  hotScore?: number
  viewCount?: number
  favoriteCount?: number
  shareCount?: number
  publishedAt?: string
  createdAt?: string
  updatedAt?: string
  tagIds?: number[]
}

export interface AdminContentSaveRequest {
  title: string
  subtitle?: string
  summary?: string
  body?: string
  searchKeywords?: string
  coverUrl?: string
  sourceName?: string
  sourceUrl?: string
  authorName?: string
  contentType: ContentType
  categoryId?: number
  status?: ContentStatus
  isOriginal?: number
  isTop?: number
  hotScore?: number
  tagIds?: number[]
}

export interface AdminContentStatusRequest {
  status: ContentStatus
}

export interface ContentListQuery extends PageQuery {
  keyword?: string
  status?: ContentStatus
  contentType?: ContentType
  categoryId?: number
}

// ============== Category ==============
// 分类状态：1=启用 0=禁用
export type CategoryStatus = 0 | 1

export interface AdminCategoryVO {
  id: number
  parentId?: number
  categoryCode: string
  categoryName: string
  icon?: string
  description?: string
  sortNo?: number
  status?: CategoryStatus
  createdAt?: string
  updatedAt?: string
}

export interface AdminCategorySaveRequest {
  id?: number
  parentCode?: string
  categoryCode: string
  categoryName: string
  icon?: string
  description?: string
  sortNo?: number
  status?: CategoryStatus
}

// ============== Tag ==============
// 标签状态：1=启用 0=禁用
export type TagStatus = 0 | 1

export interface AdminTagVO {
  id: number
  tagName: string
  tagCode: string
  useCount: number
  status?: TagStatus
  createdAt?: string
  updatedAt?: string
}

export interface AdminTagSaveRequest {
  id?: number
  tagName: string
  tagCode: string
  status?: TagStatus
}

// ============== Recommendation ==============
// 推荐位状态：1=启用 0=禁用
export type RecommendationStatus = 0 | 1

export interface AdminRecommendationVO {
  id: number
  slotCode: string
  contentId?: number
  categoryId?: number
  titleOverride?: string
  imageOverride?: string
  sortNo?: number
  startAt?: string
  endAt?: string
  status?: RecommendationStatus
  createdAt?: string
  updatedAt?: string
}

export interface AdminRecommendationSaveRequest {
  id?: number
  slotCode: string
  contentId?: number
  categoryId?: number
  titleOverride?: string
  imageOverride?: string
  sortNo?: number
  startAt?: string
  endAt?: string
  status?: RecommendationStatus
}

// ============== HotKeyword ==============
// 热词状态：1=启用 0=禁用
export type HotKeywordStatus = 0 | 1

export interface AdminHotKeywordVO {
  id: number
  keyword: string
  displayName?: string
  icon?: string
  sortNo?: number
  clickCount: number
  startAt?: string
  endAt?: string
  status?: HotKeywordStatus
  createdAt?: string
  updatedAt?: string
}

export interface AdminHotKeywordSaveRequest {
  id?: number
  keyword: string
  displayName?: string
  icon?: string
  sortNo?: number
  startAt?: string
  endAt?: string
  status?: HotKeywordStatus
}

// ============== PublicConfig ==============
export interface PublicConfigVO {
  appName: string
  slogan: string
  versionName: string
  aboutTitle: string
  aboutContent: string
  feedbackEnabled: boolean
  feedbackHint: string
  contactText: string
  announcement: string
}

export interface PublicConfigSaveRequest {
  appName: string
  slogan: string
  versionName: string
  aboutTitle: string
  aboutContent: string
  feedbackEnabled: boolean
  feedbackHint: string
  contactText: string
  announcement: string
}

// ============== Feedback ==============
export type FeedbackStatus = 'PENDING' | 'PROCESSED' | 'IGNORED'

export interface AdminFeedbackVO {
  id: number
  userId?: number
  sessionId?: string
  content: string
  contact?: string
  pagePath?: string
  submittedAt?: string
  createdAt?: string
  status: FeedbackStatus
  adminNote?: string
  handledBy?: number
  handledAt?: string
}

export interface AdminFeedbackStatusRequest {
  status: FeedbackStatus
}

export interface AdminFeedbackNoteRequest {
  adminNote: string
}

export interface FeedbackListQuery extends PageQuery {
  keyword?: string
  status?: FeedbackStatus
}

// ============== Dashboard ==============
export interface DashboardSummaryVO {
  contentTotal: number
  contentPublished: number
  contentDraft: number
  categoryTotal: number
  tagTotal: number
  feedbackPending: number
  feedbackTotal: number
  todayContentCount: number
  todayFeedbackCount: number
}
