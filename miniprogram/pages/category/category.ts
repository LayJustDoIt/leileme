import { fetchCategories, fetchSearch } from '../../services/api';
import { resolveEntryIcon } from '../../utils/format';
import type { CategoryVO, ContentCardVO } from '../../types/api';

interface CategoryPageData {
  categories: CategoryVO[];
  activeId: number;
  list: ContentCardVO[];
  page: number;
  size: number;
  total: number;
  hasNext: boolean;
  loading: boolean;
  loadingMore: boolean;
  error: string;
  listError: string;
}

const CATEGORY_BG_MAP: Record<string, string> = {
  HOT: '#fff3e5',
  CREATOR: '#f0edff',
  AI_TOOL: '#eaf2ff',
  LEARNING: '#e8faf2',
  LIFE: '#fff0f5',
  RANDOM: '#f3f4f8',
};

const PAGE_SIZE = 20;

Page<CategoryPageData, {}>({
  data: {
    categories: [],
    activeId: 0,
    list: [],
    page: 1,
    size: PAGE_SIZE,
    total: 0,
    hasNext: false,
    loading: false,
    loadingMore: false,
    error: '',
    listError: '',
  },

  onLoad() {
    this.loadCategories();
  },

  onPullDownRefresh() {
    Promise.all([this.loadCategories(false), this.loadList(true)])
      .finally(() => wx.stopPullDownRefresh());
  },

  async loadCategories(showLoading = true) {
    if (this.data.loading && showLoading) return;
    this.setData({ loading: showLoading, error: '' });
    try {
      const list = await fetchCategories();
      const enriched = (list || []).map((c) => ({
        ...c,
        _icon: resolveEntryIcon(c.icon),
        _bg: CATEGORY_BG_MAP[c.code] || '#f3f4f8',
      }));
      this.setData({
        categories: enriched,
        loading: false,
      });
      // 默认选中第一个分类
      if (enriched.length > 0 && this.data.activeId === 0) {
        this.setData({ activeId: enriched[0].id });
        this.loadList(true);
      } else if (this.data.activeId) {
        this.loadList(true);
      }
    } catch (e: any) {
      this.setData({
        loading: false,
        error: e?.message || '加载失败',
      });
    }
  },

  async loadList(isRefresh = false) {
    if (!this.data.activeId) return;
    const page = isRefresh ? 1 : this.data.page + 1;
    if (!isRefresh && (!this.data.hasNext || this.data.loadingMore)) return;
    if (isRefresh) {
      this.setData({ listError: '' });
    } else {
      this.setData({ loadingMore: true });
    }
    try {
      // V1 后端没有按分类查列表的独立接口，分类页通过搜索接口 + contentType/keyword 兜底。
      // 当前实现：使用分类 name 作为关键词调用 /search，让后端按相关度返回。
      const cat = this.data.categories.find((c) => c.id === this.data.activeId);
      const keyword = cat?.name || '';
      const res = await fetchSearch({
        keyword,
        page,
        size: PAGE_SIZE,
        categoryId: this.data.activeId,
      });
      const newList = isRefresh ? res.list : [...this.data.list, ...res.list];
      this.setData({
        list: newList,
        page: res.page,
        total: res.total,
        hasNext: res.hasNext,
        loadingMore: false,
        listError: '',
      });
    } catch (e: any) {
      this.setData({
        loadingMore: false,
        listError: e?.message || '加载失败',
      });
    }
  },

  onCategoryTap(e: WechatMiniprogram.TouchEvent) {
    const id = Number(e.currentTarget.dataset.id);
    if (!id || id === this.data.activeId) return;
    this.setData({ activeId: id, list: [], total: 0, hasNext: false });
    this.loadList(true);
  },

  onReachBottom() {
    this.loadList(false);
  },

  onRetry() {
    if (this.data.categories.length === 0) {
      this.loadCategories();
    } else {
      this.loadList(true);
    }
  },

  onContentTap(e: WechatMiniprogram.CustomEvent<{ item: ContentCardVO }>) {
    const item = e.detail.item;
    if (item && item.id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}` });
    }
  },

  onShareAppMessage() {
    return {
      title: '累了么 · 分类浏览',
      path: '/pages/category/category',
    };
  },
});
