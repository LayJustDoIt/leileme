import { get } from '../utils/request';
import { API_PATHS } from '../config/env';
import type { PublicConfigVO } from '../types/api';

// 缓存 key 与有效期（30 分钟）。升级 _v2 以避免读取旧版本残留缓存。
const CACHE_KEY = 'leileme_public_config_v2';
const LEGACY_CACHE_KEYS = ['leileme_public_config'];
const CACHE_TTL_MS = 30 * 60 * 1000;

// 最小默认配置：接口失败且无缓存时使用。
// aboutContent 提供最小可读正文，避免出现只有标题的空弹窗。
export const DEFAULT_PUBLIC_CONFIG: PublicConfigVO = {
  appName: '累了么',
  slogan: '',
  versionName: '',
  aboutTitle: '关于累了么',
  aboutContent: '累了么是一个面向碎片时间的内容发现工具。',
  feedbackEnabled: true,
  feedbackHint: '',
  contactText: '',
  announcement: null,
};

// 清理旧版本缓存 key，避免读到旧的空 aboutContent
function clearLegacyCache() {
  try {
    LEGACY_CACHE_KEYS.forEach((k) => wx.removeStorageSync(k));
  } catch (e) {
    // ignore
  }
}
clearLegacyCache();

interface CacheEntry {
  data: PublicConfigVO;
  expireAt: number;
}

function readCache(): PublicConfigVO | null {
  try {
    const raw = wx.getStorageSync(CACHE_KEY);
    if (!raw) return null;
    const entry = raw as CacheEntry;
    if (!entry || typeof entry.expireAt !== 'number') return null;
    // 缓存过期视为无缓存（但不主动清除，以便离线兜底）
    return entry.data;
  } catch (e) {
    return null;
  }
}

function writeCache(data: PublicConfigVO) {
  try {
    const entry: CacheEntry = { data, expireAt: Date.now() + CACHE_TTL_MS };
    wx.setStorageSync(CACHE_KEY, entry);
  } catch (e) {
    // 写入失败忽略
  }
}

/**
 * 获取公共配置：
 * 1. 优先读取缓存（包括已过期但未清除的缓存，作为离线兜底）
 * 2. 后台请求成功后更新缓存
 * 3. 请求失败时使用旧缓存
 * 4. 没有缓存时使用最小默认配置
 */
export function getPublicConfig(): Promise<PublicConfigVO> {
  const cached = readCache();
  return get<PublicConfigVO>(API_PATHS.publicConfig, undefined, {
    showErrorToast: false,
  })
    .then((data) => {
      if (data) {
        writeCache(data);
        return data;
      }
      return cached || DEFAULT_PUBLIC_CONFIG;
    })
    .catch(() => {
      return cached || DEFAULT_PUBLIC_CONFIG;
    });
}

/**
 * 同步读取缓存（用于页面初始化时立即展示），随后台异步刷新。
 */
export function getCachedPublicConfig(): PublicConfigVO {
  return readCache() || DEFAULT_PUBLIC_CONFIG;
}

/**
 * 强制刷新（用于下拉刷新或手动刷新）。
 */
export function refreshPublicConfig(): Promise<PublicConfigVO> {
  return get<PublicConfigVO>(API_PATHS.publicConfig, undefined, {
    showErrorToast: false,
  })
    .then((data) => {
      if (data) {
        writeCache(data);
        return data;
      }
      return readCache() || DEFAULT_PUBLIC_CONFIG;
    })
    .catch(() => {
      return readCache() || DEFAULT_PUBLIC_CONFIG;
    });
}
