import { fetchSearch, reportSearchClick } from '../../services/api';
import type { ContentCardVO, SearchFilterVO, SearchResultVO } from '../../types/api';

interface SearchPageData {
  keyword: string;
  inputKeyword: string;
  searchRequestId: string;
  list: ContentCardVO[];
  filters: SearchFilterVO[];
  selectedType: string;
  page: number;
  size: number;
  total: number;
  hasNext: boolean;
  fallback: boolean;
  loading: boolean;
  loadingMore: boolean;
  error: string;
  hasSearched: boolean;
}

const PAGE_SIZE = 20;

// 安全解码一次 URL 编码的关键词，避免重复 decode 或将 %E4... 发到后端
function safeDecode(value: string): string {
  if (!value) return '';
  try {
    return decodeURIComponent(value);
  } catch (e) {
    return value;
  }
}

Page<SearchPageData, {}>({
  data: {
    keyword: '',
    inputKeyword: '',
    searchRequestId: '',
    list: [],
    filters: [],
    selectedType: '',
    page: 1,
    size: PAGE_SIZE,
    total: 0,
    hasNext: false,
    fallback: false,
    loading: false,
    loadingMore: false,
    error: '',
    hasSearched: false,
  },

  onLoad(query: AnyObject) {
    // 跳转入口使用 encodeURIComponent(keyword) 编码，这里必须安全解码一次
    const raw = String(query?.keyword || '');
    const keyword = safeDecode(raw).trim();
    this.setData({ keyword, inputKeyword: keyword });
    if (keyword) {
      this.doSearch(keyword, 1, '');
    }
  },

  onInput(e: WechatMiniprogram.CustomEvent<{ value: string }>) {
    this.setData({ inputKeyword: e.detail.value || '' });
  },

  onClearInput() {
    this.setData({
      inputKeyword: '',
      keyword: '',
      list: [],
      filters: [],
      total: 0,
      hasSearched: false,
      selectedType: '',
      error: '',
    });
  },

  onConfirm() {
    const kw = (this.data.inputKeyword || '').trim();
    if (!kw) {
      wx.showToast({ title: '先输入你想看的内容', icon: 'none' });
      return;
    }
    if (kw === this.data.keyword && this.data.list.length > 0) {
      return;
    }
    this.doSearch(kw, 1, this.data.selectedType);
  },

  onSearchBtn() {
    this.onConfirm();
  },

  async doSearch(keyword: string, page: number, contentType: string) {
    if (this.data.loading && page === 1) return;
    const isFirstPage = page === 1;
    this.setData({
      keyword,
      loading: isFirstPage,
      loadingMore: !isFirstPage,
      error: '',
      hasSearched: true,
    });

    try {
      const res: SearchResultVO = await fetchSearch({
        keyword,
        page,
        size: PAGE_SIZE,
        contentType: contentType || undefined,
      });

      const newList = isFirstPage
        ? res.list
        : [...this.data.list, ...res.list];

      this.setData({
        searchRequestId: res.searchRequestId,
        list: newList,
        filters: res.contentTypes,
        page: res.page,
        total: res.total,
        hasNext: res.hasNext,
        fallback: res.fallback,
        loading: false,
        loadingMore: false,
      });
    } catch (e: any) {
      this.setData({
        loading: false,
        loadingMore: false,
        error: e?.message || '搜索失败',
        list: isFirstPage ? [] : this.data.list,
      });
    }
  },

  onFilterTap(e: WechatMiniprogram.TouchEvent) {
    const code = e.currentTarget.dataset.code as string;
    const target = code === this.data.selectedType ? '' : code;
    this.setData({ selectedType: target });
    this.doSearch(this.data.keyword, 1, target);
  },

  onReachBottom() {
    if (!this.data.hasNext || this.data.loadingMore || this.data.loading) return;
    this.doSearch(this.data.keyword, this.data.page + 1, this.data.selectedType);
  },

  onRetry() {
    if (this.data.inputKeyword) {
      this.doSearch(this.data.keyword || this.data.inputKeyword, 1, this.data.selectedType);
    } else {
      this.setData({ error: '', hasSearched: false });
    }
  },

  // 点击搜索结果
  onContentTap(e: WechatMiniprogram.CustomEvent<{ item: ContentCardVO; position: number }>) {
    const { item, position } = e.detail;
    if (!item || !item.id) return;

    // 异步上报，失败不阻止跳转
    if (this.data.searchRequestId) {
      reportSearchClick({
        searchRequestId: this.data.searchRequestId,
        contentId: item.id,
        position: position || 0,
      }).catch(() => {
        // 静默失败
      });
    }

    wx.navigateTo({ url: `/pages/detail/detail?id=${item.id}` });
  },

  onShareAppMessage() {
    return {
      title: `「${this.data.keyword}」搜索结果 - 累了么`,
      path: `/pages/search/search?keyword=${encodeURIComponent(this.data.keyword)}`,
    };
  },
});
