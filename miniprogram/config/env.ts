// 环境配置 - 不同环境走不同后端地址。
// 微信开发者工具勾选「不校验合法域名」可使用 http://localhost。

interface EnvConfig {
  apiBase: string;
  requestTimeout: number;
  envName: 'dev' | 'prod';
}

// 当前激活环境，可在开发者工具的编译模式中通过 globalData 切换
const ACTIVE_ENV: 'dev' | 'prod' = 'dev';

const ENV_MAP: Record<'dev' | 'prod', EnvConfig> = {
  dev: {
    apiBase: 'http://localhost:8080',
    requestTimeout: 10000,
    envName: 'dev',
  },
  prod: {
    apiBase: 'https://leileme.example.com',
    requestTimeout: 10000,
    envName: 'prod',
  },
};

export const env: EnvConfig = ENV_MAP[ACTIVE_ENV];

export const API_PREFIX = '/api/v1';

// 完整接口地址常量，避免散落在各页面
export const API_PATHS = {
  home: `${API_PREFIX}/home`,
  search: `${API_PREFIX}/search`,
  searchClick: `${API_PREFIX}/search/click`,
  contents: (id: number | string) => `${API_PREFIX}/contents/${id}`,
  categories: `${API_PREFIX}/categories`,
  randomTip: `${API_PREFIX}/random-tip`,
  // 登录
  authWechatLogin: `${API_PREFIX}/auth/wechat-login`,
  // 收藏
  contentFavorite: (id: number | string) => `${API_PREFIX}/contents/${id}/favorite`,
  // 我的
  meFavorites: `${API_PREFIX}/me/favorites`,
  meHistory: `${API_PREFIX}/me/history`,
  meHistoryItem: (contentId: number | string) => `${API_PREFIX}/me/history/${contentId}`,
} as const;
