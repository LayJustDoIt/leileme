// 后端 API 响应类型定义（与后端 VO 字段保持一致）

// 通用内容卡片
export interface ContentCardVO {
  id: number;
  contentType: string;
  title: string;
  summary?: string;
  coverUrl?: string;
  sourceName?: string;
  tags?: string[];
  viewCount: number;
  favoriteCount: number;
  publishedAt?: string;
  favorite: boolean;
}

// 首页
export interface HotKeywordVO {
  keyword: string;
  displayName: string;
  icon?: string | null;
}

export interface SceneEntryVO {
  code: string;
  name: string;
  icon: string;
  searchKeyword: string;
}

export interface AudienceVO {
  id: number;
  code: string;
  name: string;
  description: string;
}

export interface HomeVO {
  greeting: string;
  hotKeywords: HotKeywordVO[];
  sceneEntries: SceneEntryVO[];
  todayRecommendations: ContentCardVO[];
  audiences: AudienceVO[];
  guessYouLike: ContentCardVO[];
}

// 搜索
export interface SearchFilterVO {
  code: string;
  name: string;
  count: number;
}

export interface SearchResultVO {
  searchRequestId: string;
  keyword: string;
  page: number;
  size: number;
  total: number;
  hasNext: boolean;
  contentTypes: SearchFilterVO[];
  list: ContentCardVO[];
  fallback: boolean;
}

export interface SearchClickRequest {
  searchRequestId: string;
  contentId: number;
  position?: number;
  sessionId?: string;
}

// 详情
export interface ContentDetailVO {
  id: number;
  contentType: string;
  title: string;
  summary?: string;
  body?: string;
  coverUrl?: string;
  sourceName?: string;
  sourceUrl?: string;
  authorName?: string;
  tags?: string[];
  viewCount: number;
  favoriteCount: number;
  shareCount: number;
  publishedAt?: string;
  favorite: boolean;
  relatedContents: ContentCardVO[];
}

// 分类
export interface CategoryVO {
  id: number;
  code: string;
  name: string;
  icon?: string;
  description?: string;
  sortNo: number;
}

// 随机建议
export interface RandomTipVO {
  id: number;
  title: string;
  summary?: string;
}

// 搜索请求参数
export interface SearchParams {
  keyword: string;
  page?: number;
  size?: number;
  contentType?: string;
  categoryId?: number;
  sessionId?: string;
  userId?: number;
}
