import { fetchRandomTip } from '../../services/api';
import { env } from '../../config/env';
import type { RandomTipVO } from '../../types/api';

interface MinePageData {
  sessionId: string;
  envName: string;
  apiBase: string;
  randomTip: RandomTipVO | null;
  tipLoading: boolean;
  menuList: Array<{ key: string; label: string; icon: string; desc: string; soon?: boolean }>;
}

Page<MinePageData, {}>({
  data: {
    sessionId: '',
    envName: env.envName,
    apiBase: env.apiBase,
    randomTip: null,
    tipLoading: false,
    menuList: [
      { key: 'favorite', label: '我的收藏', icon: '❤️', desc: '登录后查看收藏内容', soon: true },
      { key: 'history', label: '浏览历史', icon: '🕒', desc: '近期看过的内容', soon: true },
      { key: 'feedback', label: '反馈建议', icon: '✉️', desc: '告诉我们你的想法', soon: true },
      { key: 'about', label: '关于累了么', icon: '🌟', desc: 'V1 · 公益向内容工具' },
    ],
  },

  onLoad() {
    const app = getApp<IAppOption>();
    this.setData({ sessionId: app?.globalData?.sessionId || '' });
    this.loadTip();
  },

  onShow() {
    const app = getApp<IAppOption>();
    const sid = app?.globalData?.sessionId || '';
    if (sid && sid !== this.data.sessionId) {
      this.setData({ sessionId: sid });
    }
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

  onMenuTap(e: WechatMiniprogram.TouchEvent) {
    const key = e.currentTarget.dataset.key as string;
    const item = this.data.menuList.find((m) => m.key === key);
    if (!item) return;
    if (item.soon) {
      wx.showToast({ title: '功能即将上线', icon: 'none' });
      return;
    }
    if (key === 'about') {
      wx.showModal({
        title: '关于累了么',
        content: '累了么 V1 是一个面向碎片时间的内容工具，聚合热搜、文案、AI 工具与学习资源。\n\n公益向，无需登录即可浏览，后续会逐步加入收藏与个性化推荐。',
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
