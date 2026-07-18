import { fetchContentDetail } from '../../services/api';
import { favoriteContent, unfavoriteContent } from '../../services/auth';
import { isLoggedIn } from '../../utils/auth';
import { formatCount, formatTime, contentTypeName, contentTypeColor, contentTypeIcon } from '../../utils/format';
import { AuthError } from '../../utils/request';
import type { ContentDetailVO } from '../../types/api';

interface DetailPageData {
  id: number | string;
  detail: ContentDetailVO | null;
  loading: boolean;
  error: string;
  typeName: string;
  typeColor: string;
  typeIcon: string;
  viewText: string;
  favText: string;
  shareText: string;
  timeText: string;
  favorite: boolean;
  favLoading: boolean;
}

Page<DetailPageData, {}>({
  data: {
    id: 0,
    detail: null,
    loading: false,
    error: '',
    typeName: '',
    typeColor: '',
    typeIcon: '',
    viewText: '',
    favText: '',
    shareText: '',
    timeText: '',
    favorite: false,
    favLoading: false,
  },

  onLoad(query: AnyObject) {
    const id = (query?.id as string | number) || 0;
    this.setData({ id });
    this.loadDetail(id);
  },

  onShow() {
    wx.showShareMenu({ withShareTicket: true, menus: ['shareAppMessage', 'shareTimeline'] });
  },

  async loadDetail(id: number | string) {
    if (!id) {
      this.setData({ error: '内容不存在' });
      return;
    }
    this.setData({ loading: true, error: '' });
    try {
      const detail = await fetchContentDetail(id);
      this.setData({
        detail,
        loading: false,
        typeName: contentTypeName(detail.contentType),
        typeColor: contentTypeColor(detail.contentType),
        typeIcon: contentTypeIcon(detail.contentType),
        viewText: formatCount(detail.viewCount) + '阅读',
        favText: formatCount(detail.favoriteCount) + '收藏',
        shareText: formatCount(detail.shareCount) + '分享',
        timeText: formatTime(detail.publishedAt),
        favorite: detail.favorite,
      });
      wx.setNavigationBarTitle({ title: detail.title ? detail.title.slice(0, 16) : '内容详情' });
    } catch (e: any) {
      this.setData({
        loading: false,
        error: e?.message || '加载失败',
      });
    }
  },

  onRetry() {
    this.loadDetail(this.data.id);
  },

  async onToggleFavorite() {
    // 防止重复提交
    if (this.data.favLoading) return;

    // 未登录引导登录
    if (!isLoggedIn()) {
      wx.showToast({ title: '请先登录后再收藏', icon: 'none', duration: 1800 });
      return;
    }

    const id = this.data.id;
    const wasFavorite = this.data.favorite;
    const oldFavText = this.data.favText;
    const newFavCount = wasFavorite
      ? Math.max((this.data.detail?.favoriteCount || 0) - 1, 0)
      : (this.data.detail?.favoriteCount || 0) + 1;

    // 乐观更新
    this.setData({
      favLoading: true,
      favorite: !wasFavorite,
      favText: formatCount(newFavCount) + '收藏',
    });

    try {
      if (wasFavorite) {
        await unfavoriteContent(id);
      } else {
        await favoriteContent(id);
      }
      this.setData({ favLoading: false });
    } catch (e: any) {
      // 请求失败恢复按钮原状态
      this.setData({
        favLoading: false,
        favorite: wasFavorite,
        favText: oldFavText,
      });
      if (e instanceof AuthError) {
        wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
      } else {
        wx.showToast({ title: e?.message || '操作失败，请重试', icon: 'none' });
      }
    }
  },

  onShareTap() {
    wx.showToast({ title: '点击右上角分享给朋友', icon: 'none' });
  },

  onRelatedTap(e: WechatMiniprogram.CustomEvent<{ item: { id: number } }>) {
    const item = e.detail.item;
    if (item && item.id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}` });
    }
  },

  onShareAppMessage() {
    const d = this.data.detail;
    if (!d) {
      return { title: '累了么 · 内容详情', path: '/pages/index/index' };
    }
    return {
      title: d.title,
      path: `/pages/detail/detail?id=${d.id}`,
      imageUrl: d.coverUrl,
    };
  },

  onShareTimeline() {
    const d = this.data.detail;
    return {
      title: d?.title || '累了么',
      query: `id=${this.data.id}`,
      imageUrl: d?.coverUrl,
    };
  },
});
