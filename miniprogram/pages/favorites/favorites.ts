import { fetchFavoriteList, unfavoriteContent } from '../../services/auth';
import { AuthError } from '../../utils/request';
import type { ContentCardVO } from '../../types/api';

interface FavoritesPageData {
  list: ContentCardVO[];
  page: number;
  size: number;
  total: number;
  hasNext: boolean;
  loading: boolean;
  loadingMore: boolean;
  error: string;
  // 正在取消收藏的内容 ID，防止重复提交
  operatingId: number;
}

const PAGE_SIZE = 20;

Page<FavoritesPageData, {}>({
  data: {
    list: [],
    page: 1,
    size: PAGE_SIZE,
    total: 0,
    hasNext: false,
    loading: false,
    loadingMore: false,
    error: '',
    operatingId: 0,
  },

  onLoad() {
    this.loadList(true);
  },

  onShow() {
    // 从详情页返回后静默刷新（收藏状态可能已变化）。
    // 首次进入时 onLoad 已触发加载（loading=true），会被 loadList 内部 guard 拦截。
    this.loadList(true, true);
  },

  onPullDownRefresh() {
    this.loadList(true, true).finally(() => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    this.loadList(false);
  },

  async loadList(isRefresh = false, silent = false) {
    if (isRefresh) {
      if (this.data.loading) return;
      this.setData({ loading: !silent, error: '' });
    } else {
      if (!this.data.hasNext || this.data.loadingMore || this.data.loading) return;
      this.setData({ loadingMore: true });
    }
    const page = isRefresh ? 1 : this.data.page + 1;
    try {
      const res = await fetchFavoriteList(page, PAGE_SIZE);
      const newList = isRefresh ? res.list : [...this.data.list, ...res.list];
      this.setData({
        list: newList,
        page: res.page,
        total: res.total,
        hasNext: res.hasNext,
        loading: false,
        loadingMore: false,
        error: '',
      });
    } catch (e: any) {
      this.setData({
        loading: false,
        loadingMore: false,
        error: e?.message || '加载失败',
      });
    }
  },

  onRetry() {
    this.loadList(true);
  },

  onContentTap(e: WechatMiniprogram.CustomEvent<{ item: ContentCardVO }>) {
    const item = e.detail.item;
    if (item && item.id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}` });
    }
  },

  async onUnfavoriteTap(e: WechatMiniprogram.TouchEvent) {
    const id = Number(e.currentTarget.dataset.id);
    if (!id) return;
    if (this.data.operatingId === id) return;

    const snapshot = this.data.list;
    const oldTotal = this.data.total;
    // 乐观移除：立即从列表移除
    this.setData({
      operatingId: id,
      list: snapshot.filter((c) => c.id !== id),
      total: Math.max(oldTotal - 1, 0),
    });

    try {
      await unfavoriteContent(id);
      this.setData({ operatingId: 0 });
      wx.showToast({ title: '已取消收藏', icon: 'none', duration: 1500 });
    } catch (err: any) {
      // 请求失败恢复按钮原状态（回滚列表）
      this.setData({
        operatingId: 0,
        list: snapshot,
        total: oldTotal,
      });
      if (err instanceof AuthError) {
        wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
      } else {
        wx.showToast({ title: err?.message || '操作失败', icon: 'none' });
      }
    }
  },

  onShareAppMessage() {
    return {
      title: '累了么 · 我的收藏',
      path: '/pages/index/index',
    };
  },
});
