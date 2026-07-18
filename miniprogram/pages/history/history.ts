import { fetchHistoryList, deleteHistoryItem, clearHistory } from '../../services/auth';
import { AuthError } from '../../utils/request';
import { formatTime } from '../../utils/format';
import type { HistoryItemVO } from '../../types/api';

// 历史列表项（带预处理的展示字段）
type HistoryDisplayItem = HistoryItemVO & {
  _lastText: string;
  _browseText: string;
};

interface HistoryPageData {
  list: HistoryDisplayItem[];
  page: number;
  size: number;
  total: number;
  hasNext: boolean;
  loading: boolean;
  loadingMore: boolean;
  error: string;
  // 正在删除的内容 ID，防止重复提交
  operatingId: number;
  // 清空中
  clearing: boolean;
}

const PAGE_SIZE = 20;

Page<HistoryPageData, {}>({
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
    clearing: false,
  },

  onLoad() {
    this.loadList(true);
  },

  onShow() {
    // 从详情页返回后静默刷新（浏览次数 / 最近时间可能已变化）。
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
      const res = await fetchHistoryList(page, PAGE_SIZE);
      // 预处理：格式化最近浏览时间和次数
      const enriched: HistoryDisplayItem[] = (res.list || []).map((h) => ({
        ...h,
        _lastText: formatTime(h.lastBrowsedAt),
        _browseText: '浏览 ' + (h.browseCount || 1) + ' 次',
      }));
      const newList = isRefresh ? enriched : [...this.data.list, ...enriched];
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

  onContentTap(e: WechatMiniprogram.CustomEvent<{ item: HistoryItemVO }>) {
    const item = e.detail.item;
    if (item && item.id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}` });
    }
  },

  async onDeleteTap(e: WechatMiniprogram.TouchEvent) {
    const id = Number(e.currentTarget.dataset.id);
    if (!id) return;
    if (this.data.operatingId === id) return;

    const snapshot = this.data.list;
    const oldTotal = this.data.total;
    // 乐观移除
    this.setData({
      operatingId: id,
      list: snapshot.filter((h) => h.id !== id),
      total: Math.max(oldTotal - 1, 0),
    });

    try {
      await deleteHistoryItem(id);
      this.setData({ operatingId: 0 });
      wx.showToast({ title: '已删除', icon: 'none', duration: 1500 });
    } catch (err: any) {
      // 回滚
      this.setData({
        operatingId: 0,
        list: snapshot,
        total: oldTotal,
      });
      if (err instanceof AuthError) {
        wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
      } else {
        wx.showToast({ title: err?.message || '删除失败', icon: 'none' });
      }
    }
  },

  onClearTap() {
    if (this.data.clearing) return;
    if (this.data.list.length === 0) return;
    // 清空历史需要二次确认
    wx.showModal({
      title: '清空浏览历史',
      content: '确定要清空所有浏览历史吗？此操作不可恢复。',
      confirmText: '清空',
      confirmColor: '#ff6b6b',
      success: async (res) => {
        if (!res.confirm) return;
        this.setData({ clearing: true });
        try {
          await clearHistory();
          this.setData({
            list: [],
            total: 0,
            hasNext: false,
            page: 1,
            clearing: false,
          });
          wx.showToast({ title: '已清空浏览历史', icon: 'none', duration: 1500 });
        } catch (err: any) {
          this.setData({ clearing: false });
          if (err instanceof AuthError) {
            wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
          } else {
            wx.showToast({ title: err?.message || '清空失败', icon: 'none' });
          }
        }
      },
    });
  },

  onShareAppMessage() {
    return {
      title: '累了么 · 浏览历史',
      path: '/pages/index/index',
    };
  },
});
