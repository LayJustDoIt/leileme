import { fetchHome, fetchRandomTip } from '../../services/api';
import { dynamicGreeting, weekdayText, resolveEntryIcon } from '../../utils/format';
import type { HomeVO, RandomTipVO } from '../../types/api';

interface IndexData {
  greeting: string;
  weekday: string;
  home: HomeVO | null;
  randomTip: RandomTipVO | null;
  loading: boolean;
  loadingMore: boolean;
  error: string;
  tipRefreshing: boolean;
}

Page<IndexData, {}>({
  data: {
    greeting: '',
    weekday: '',
    home: null,
    randomTip: null,
    loading: false,
    loadingMore: false,
    error: '',
    tipRefreshing: false,
  },

  onLoad() {
    this.setData({
      greeting: dynamicGreeting(),
      weekday: weekdayText(),
    });
    this.loadAll();
  },

  onShow() {
    // 每次显示重新计算问候语
    const greeting = dynamicGreeting();
    if (greeting !== this.data.greeting) {
      this.setData({ greeting, weekday: weekdayText() });
    }
  },

  onPullDownRefresh() {
    this.loadAll(true).finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  async loadAll(isRefresh = false) {
    if (this.data.loading) return;
    this.setData({ loading: true, error: '' });
    try {
      const [home, randomTip] = await Promise.all([
        fetchHome(),
        fetchRandomTip().catch(() => null),
      ]);
      // 场景入口 icon 是后端 code（money/book/...），统一映射成 emoji
      if (home && home.sceneEntries) {
        home.sceneEntries = home.sceneEntries.map((s) => ({
          ...s,
          icon: resolveEntryIcon(s.icon),
        }));
      }
      this.setData({
        home,
        randomTip,
        loading: false,
      });
      if (isRefresh) {
        wx.showToast({ title: '已更新', icon: 'success', duration: 1200 });
      }
    } catch (e: any) {
      this.setData({
        loading: false,
        error: e?.message || '加载失败',
      });
    }
  },

  onRetry() {
    this.loadAll();
  },

  async refreshTip() {
    if (this.data.tipRefreshing) return;
    this.setData({ tipRefreshing: true });
    try {
      const tip = await fetchRandomTip();
      this.setData({ randomTip: tip, tipRefreshing: false });
    } catch (e) {
      this.setData({ tipRefreshing: false });
      wx.showToast({ title: '换个试试', icon: 'none' });
    }
  },

  // 输入框点击 -> 跳转搜索页
  onTapSearch() {
    wx.navigateTo({ url: '/pages/search/search' });
  },

  // 热门词点击 -> 直接进入搜索页带 keyword
  onTapHotKeyword(e: WechatMiniprogram.TouchEvent) {
    const keyword = e.currentTarget.dataset.keyword as string;
    wx.navigateTo({
      url: `/pages/search/search?keyword=${encodeURIComponent(keyword)}`,
    });
  },

  // 场景入口
  onTapScene(e: WechatMiniprogram.TouchEvent) {
    const keyword = e.currentTarget.dataset.keyword as string;
    wx.navigateTo({
      url: `/pages/search/search?keyword=${encodeURIComponent(keyword)}`,
    });
  },

  // 人群入口
  onTapAudience(e: WechatMiniprogram.TouchEvent) {
    const name = e.currentTarget.dataset.name as string;
    wx.navigateTo({
      url: `/pages/search/search?keyword=${encodeURIComponent(name)}`,
    });
  },

  // 今日推荐卡片点击
  onTapRecommend(e: WechatMiniprogram.TouchEvent) {
    const id = e.currentTarget.dataset.id as number;
    if (id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${id}` });
    }
  },

  // 猜你喜欢内容卡片点击
  onContentTap(e: WechatMiniprogram.CustomEvent) {
    const item = e.detail.item;
    if (item && item.id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}` });
    }
  },

  onShareAppMessage() {
    return {
      title: '累了么 · 你的碎片时间搭子',
      path: '/pages/index/index',
    };
  },
});
