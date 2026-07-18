import { get, post, del } from '../utils/request';
import { API_PATHS } from '../config/env';
import { getSessionId } from '../utils/session';
import type { LoginResultVO, PageResultVO, ContentCardVO, HistoryItemVO } from '../types/api';

/**
 * 微信登录：wx.login 获取 code → 调用后端换 token
 */
export function wechatLogin(): Promise<LoginResultVO> {
  return new Promise<LoginResultVO>((resolve, reject) => {
    wx.login({
      success(res) {
        if (!res.code) {
          reject(new Error('wx.login 未返回 code'));
          return;
        }
        const body = {
          code: res.code,
          sessionId: getSessionId(),
        };
        post<LoginResultVO>(API_PATHS.authWechatLogin, body, {
          showLoading: true,
          loadingText: '登录中',
        }).then(resolve).catch(reject);
      },
      fail(err) {
        reject(new Error(err.errMsg || 'wx.login 失败'));
      },
    });
  });
}

/**
 * 收藏内容
 */
export function favoriteContent(id: number | string): Promise<void> {
  return post<void>(API_PATHS.contentFavorite(id), {});
}

/**
 * 取消收藏
 */
export function unfavoriteContent(id: number | string): Promise<void> {
  return del<void>(API_PATHS.contentFavorite(id));
}

/**
 * 收藏列表
 */
export function fetchFavoriteList(page = 1, size = 20): Promise<PageResultVO<ContentCardVO>> {
  return get<PageResultVO<ContentCardVO>>(API_PATHS.meFavorites, { page, size });
}

/**
 * 浏览历史列表
 */
export function fetchHistoryList(page = 1, size = 20): Promise<PageResultVO<HistoryItemVO>> {
  return get<PageResultVO<HistoryItemVO>>(API_PATHS.meHistory, { page, size });
}

/**
 * 删除单条浏览历史
 */
export function deleteHistoryItem(contentId: number | string): Promise<void> {
  return del<void>(API_PATHS.meHistoryItem(contentId));
}

/**
 * 清空浏览历史
 */
export function clearHistory(): Promise<void> {
  return del<void>(API_PATHS.meHistory);
}
