import { formatCount, formatTime, contentTypeColor } from '../../utils/format';
import type { ContentCardVO } from '../../types/api';

// 内容类型 -> 默认封面展示信息
const TYPE_MAP: Record<string, { icon: string; name: string; className: string }> = {
  HOT_NEWS: { icon: '🔥', name: '热点资讯', className: 'cover-hot' },
  COPYWRITING: { icon: '✍️', name: '文案脚本', className: 'cover-copy' },
  TOOL: { icon: '🧰', name: '工具资源', className: 'cover-tool' },
  GUIDE: { icon: '📚', name: '方法教程', className: 'cover-guide' },
  RANDOM_TIP: { icon: '🎲', name: '随机建议', className: 'cover-random' },
};

const DEFAULT_TYPE_INFO = { icon: '✨', name: '精选内容', className: 'cover-default' };

Component({
  options: {
    addGlobalClass: true,
    multipleSlots: true,
  },
  properties: {
    item: {
      type: Object,
      value: {} as ContentCardVO,
      observer(item: ContentCardVO) {
        if (!item || !item.id) return;

        const coverUrl = item.coverUrl || '';
        // 占位图（placehold.co）视为无封面，走默认封面分支
        const hasCover = Boolean(coverUrl) && !coverUrl.includes('placehold.co');

        const typeInfo = TYPE_MAP[item.contentType] || DEFAULT_TYPE_INFO;

        this.setData({
          hasCover,
          typeIcon: typeInfo.icon,
          typeName: typeInfo.name,
          typeClass: typeInfo.className,
          typeColor: contentTypeColor(item.contentType),
          viewText: formatCount(item.viewCount) + '看过',
          favText: formatCount(item.favoriteCount) + '收藏',
          timeText: formatTime(item.publishedAt),
        });
      },
    },
    showTag: {
      type: Boolean,
      value: true,
    },
    position: {
      type: Number,
      value: 0,
    },
  },
  data: {
    typeName: '',
    typeColor: '',
    typeIcon: '',
    typeClass: '',
    viewText: '',
    favText: '',
    timeText: '',
    hasCover: false,
  },
  methods: {
    onTap() {
      const item = this.data.item as ContentCardVO;
      if (!item || !item.id) return;
      this.triggerEvent('tap', { item, position: this.data.position });
    },
    onImageError() {
      this.setData({ hasCover: false });
    },
  },
});
