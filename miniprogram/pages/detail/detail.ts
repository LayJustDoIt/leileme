import { fetchContentDetail } from '../../services/api';
import { formatCount, formatTime, contentTypeName, contentTypeColor, contentTypeIcon } from '../../utils/format';
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

  onToggleFavorite() {
    // V1 阶段未接登录，不展示收藏成功状态
    wx.showToast({
      title: '登录功能将在下一阶段开放',
      icon: 'none',
      duration: 1800,
    });
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
