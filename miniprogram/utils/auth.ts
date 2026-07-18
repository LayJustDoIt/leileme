// 登录态管理：token 持久化与读取

const TOKEN_KEY = 'leileme_access_token';
const USER_KEY = 'leileme_user_info';

export interface UserInfo {
  id: number;
  nickname: string | null;
  avatarUrl: string | null;
}

/**
 * 从 storage 读取 accessToken
 */
export function getToken(): string {
  try {
    return wx.getStorageSync(TOKEN_KEY) || '';
  } catch (e) {
    return '';
  }
}

/**
 * 持久化 accessToken
 */
export function setToken(token: string): void {
  try {
    wx.setStorageSync(TOKEN_KEY, token);
  } catch (e) {
    // 写入失败忽略
  }
}

/**
 * 清除 accessToken
 */
export function clearToken(): void {
  try {
    wx.removeStorageSync(TOKEN_KEY);
  } catch (e) {
    // 忽略
  }
}

/**
 * 从 storage 读取用户信息
 */
export function getUser(): UserInfo | null {
  try {
    const u = wx.getStorageSync(USER_KEY);
    return u || null;
  } catch (e) {
    return null;
  }
}

/**
 * 持久化用户信息
 */
export function setUser(user: UserInfo): void {
  try {
    wx.setStorageSync(USER_KEY, user);
  } catch (e) {
    // 忽略
  }
}

/**
 * 清除用户信息
 */
export function clearUser(): void {
  try {
    wx.removeStorageSync(USER_KEY);
  } catch (e) {
    // 忽略
  }
}

/**
 * 是否已登录
 */
export function isLoggedIn(): boolean {
  return !!getToken();
}

/**
 * 清除所有登录态
 */
export function clearAuth(): void {
  clearToken();
  clearUser();
}
