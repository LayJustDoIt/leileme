import { get, post } from '../utils/request';
import { API_PATHS } from '../config/env';
import { getSessionId } from '../utils/session';
import type {
  HomeVO,
  SearchResultVO,
  SearchClickRequest,
  ContentDetailVO,
  CategoryVO,
  RandomTipVO,
  SearchParams,
} from '../types/api';

export function fetchHome(): Promise<HomeVO> {
  return get<HomeVO>(API_PATHS.home);
}

export function fetchSearch(params: SearchParams): Promise<SearchResultVO> {
  const { keyword, page = 1, size = 20, contentType, categoryId, sessionId, userId } = params;
  const data: Record<string, any> = {
    keyword,
    page,
    size,
    sessionId: sessionId || getSessionId(),
  };
  if (contentType) data.contentType = contentType;
  if (categoryId !== undefined && categoryId !== null) data.categoryId = categoryId;
  if (userId) data.userId = userId;
  return get<SearchResultVO>(API_PATHS.search, data);
}

export function reportSearchClick(payload: SearchClickRequest): Promise<void> {
  const body: SearchClickRequest = {
    ...payload,
    sessionId: payload.sessionId || getSessionId(),
  };
  return post<void>(API_PATHS.searchClick, body as unknown as Record<string, any>, {
    showErrorToast: false,
  });
}

export function fetchContentDetail(id: number | string, sessionId?: string): Promise<ContentDetailVO> {
  const data = { sessionId: sessionId || getSessionId() };
  return get<ContentDetailVO>(API_PATHS.contents(id), data);
}

export function fetchCategories(): Promise<CategoryVO[]> {
  return get<CategoryVO[]>(API_PATHS.categories);
}

export function fetchRandomTip(): Promise<RandomTipVO> {
  return get<RandomTipVO>(API_PATHS.randomTip);
}
