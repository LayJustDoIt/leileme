import { fetchRandomTip } from '../../services/api';
import { wechatLogin } from '../../services/auth';
import { isLoggedIn, getUser, setToken, setUser, clearAuth, type UserInfo } from '../../utils/auth';
import { env } from '../../config/env';
import type { RandomTipVO } from '../../types/api';

interface MinePageData {
  sessionId: string;
  envName: string;
  apiBase: string;
  randomTip: RandomTipVO | null;
  tipLoading: boolean;
  loggedIn: boolean;
  user: UserInfo | null;
  loginLoading: boolean;
  menuList: Array<{ key: string; label: string; icon: string; desc: string; soon?: boolean }>;
}

Page<MinePageData, {}>({
  data: {
    sessionId: '',
    envName: env.envName,
    apiBase: env.apiBase,
    randomTip: null,
    tipLoading: false,
    loggedIn: false,
    user: null,
    loginLoading: false,
    menuList: [
      { key: 'favorite', label: '我的收藏', icon: '❤️', desc: '收藏的内容' },
      { key: 'history', label: '浏览历史', icon: '🕒', desc: '近期看过的内容' },
      { key: 'feedback', label: '反馈建议', icon: '✉️', desc: '告诉我们你的想法', soon: true },
      { key: 'about', label: '关于累了么', icon: '🌟', desc: 'V1 · 公益向内容工具' },
    ],
  },

  onLoad() {
    const app = getApp<IAppOption>();
    this.setData({ sessionId: app?.globalData?.sessionId || '' });
    this.refreshLoginState();
    this.loadTip();
  },

  onShow() {
    const app = getApp<IAppOption>();
    const sid = app?.globalData?.sessionId || '';
    if (sid && sid !== this.data.sessionId) {
      this.setData({ sessionId: sid });
    }
    // 每次显示时刷新登录态（可能在其他页面登录或退出）
    this.refreshLoginState();
  },

  refreshLoginState() {
    this.setData({
      loggedIn: isLoggedIn(),
      user: getUser(),
    });
  },

  async loadTip() {
    if (this.data.tipLoading) return;
    this.setData({ tipLoading: true });
    try {
      const tip = await fetchRandomTip();
      this.setData({ randomTip: tip, tipLoading: false });
    } catch (e) {
      this.setData({ tipLoading: false });
    }
  },

  onRefreshTip() {
    this.loadTip();
  },

  async onLoginTap() {
    // 防止重复点击
    if (this.data.loginLoading) return;
    this.setData({ loginLoading: true });
    try {
      const result = await wechatLogin();
      setToken(result.accessToken);
      setUser(result.user);
      this.setData({
        loggedIn: true,
        user: result.user,
        loginLoading: false,
      });
      wx.showToast({ title: '登录成功', icon: 'success', duration: 1500 });
    } catch (e: any) {
      // 登录失败不修改原状态
      this.setData({ loginLoading: false });
      const msg = e?.message || '登录失败，请稍后再试';
      wx.showToast({ title: msg, icon: 'none', duration: 2000 });
    }
  },

  onLogoutTap() {
    wx.showModal({
      title: '退出登录',
      content: '退出后收藏和浏览历史将无法访问，确定退出吗？',
      confirmText: '退出',
      confirmColor: '#ff6b6b',
      success: (res) => {
        if (res.confirm) {
          clearAuth();
          this.setData({
            loggedIn: false,
            user: null,
          });
          wx.showToast({ title: '已退出登录', icon: 'none', duration: 1500 });
        }
      },
    });
  },

  onMenuTap(e: WechatMiniprogram.TouchEvent) {
    const key = e.currentTarget.dataset.key as string;
    const item = this.data.menuList.find((m) => m.key === key);
    if (!item) return;
    if (item.soon) {
      wx.showToast({ title: '功能即将上线', icon: 'none' });
      return;
    }
    // 收藏和历史需要登录
    if ((key === 'favorite' || key === 'history') && !this.data.loggedIn) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    if (key === 'favorite') {
      wx.navigateTo({ url: '/pages/favorites/favorites' });
      return;
    }
    if (key === 'history') {
      wx.navigateTo({ url: '/pages/history/history' });
      return;
    }
    if (key === 'about') {
      wx.showModal({
        title: '关于累了么',
        content: '累了么是一个面向碎片时间的内容工具，聚合热搜、文案、工具与学习资源。\n\n公益向，支持微信登录后收藏与浏览历史。',
        showCancel: false,
        confirmText: '知道了',
      });
    }
  },

  onCopySession() {
    wx.setStorageSync('leileme_session_id', this.data.sessionId);
    wx.showToast({ title: '已复制 sessionId', icon: 'none' });
  },

  onShareAppMessage() {
    return {
      title: '累了么 · 你的碎片时间搭子',
      path: '/pages/index/index',
    };
  },
});
