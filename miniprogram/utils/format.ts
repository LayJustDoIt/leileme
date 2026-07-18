// 数字与时间格式化工具

export function formatCount(n: number | undefined | null): string {
  if (n === undefined || n === null) return '0';
  if (n < 1000) return String(n);
  if (n < 10000) return (n / 1000).toFixed(1) + 'k';
  return (n / 10000).toFixed(1) + '万';
}

export function formatTime(input?: string | Date | null): string {
  if (!input) return '';
  const date = typeof input === 'string' ? new Date(input.replace(/-/g, '/')) : input;
  if (isNaN(date.getTime())) return '';
  const now = Date.now();
  const ts = date.getTime();
  const diff = now - ts;
  if (diff < 60 * 1000) return '刚刚';
  if (diff < 60 * 60 * 1000) return Math.floor(diff / (60 * 1000)) + '分钟前';
  if (diff < 24 * 60 * 60 * 1000) return Math.floor(diff / (60 * 60 * 1000)) + '小时前';
  if (diff < 7 * 24 * 60 * 60 * 1000) return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前';
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

// 动态问候语，按当前小时返回
export function dynamicGreeting(): string {
  const h = new Date().getHours();
  if (h >= 5 && h < 11) return '早上好，今天想先做什么？';
  if (h >= 11 && h < 14) return '中午好，休息一下再出发。';
  if (h >= 14 && h < 18) return '下午好，累了就来逛一会儿。';
  if (h >= 18 && h < 23) return '晚上好，看看今天有什么新鲜事。';
  return '夜深了，别太累，随便看看吧。';
}

export function weekdayText(): string {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[new Date().getDay()] + ' · 慢一点';
}

// 内容类型 -> 中文显示名（与后端 SearchService.TYPE_NAMES 保持一致）
const CONTENT_TYPE_NAMES: Record<string, string> = {
  HOT_NEWS: '热点资讯',
  COPYWRITING: '文案脚本',
  TOOL: '工具资源',
  GUIDE: '方法教程',
  RANDOM_TIP: '随机建议',
};

export function contentTypeName(code: string): string {
  return CONTENT_TYPE_NAMES[code] || code;
}

// 内容类型对应的主题色（用于卡片左侧色块）
const CONTENT_TYPE_COLORS: Record<string, string> = {
  HOT_NEWS: '#ff7c67',
  COPYWRITING: '#ff9c66',
  TOOL: '#547cff',
  GUIDE: '#22b573',
  RANDOM_TIP: '#8b7dff',
};

export function contentTypeColor(code: string): string {
  return CONTENT_TYPE_COLORS[code] || '#8b90a0';
}

// 内容类型对应的 emoji
const CONTENT_TYPE_ICONS: Record<string, string> = {
  HOT_NEWS: '🔥',
  COPYWRITING: '✍️',
  TOOL: '🤖',
  GUIDE: '🧭',
  RANDOM_TIP: '🎲',
};

export function contentTypeIcon(code: string): string {
  return CONTENT_TYPE_ICONS[code] || '📄';
}

// 内容类型对应的默认封面渐变（无 coverUrl 时使用）
const CONTENT_TYPE_GRADIENTS: Record<string, string> = {
  HOT_NEWS: 'linear-gradient(135deg, #ff7c67, #ff9c66)',
  COPYWRITING: 'linear-gradient(135deg, #ff9f43, #ffbf6b)',
  TOOL: 'linear-gradient(135deg, #547cff, #7e8fff)',
  GUIDE: 'linear-gradient(135deg, #20b486, #4fd0a2)',
  RANDOM_TIP: 'linear-gradient(135deg, #8b7dff, #a190ff)',
};

export function contentTypeGradient(code: string): string {
  return CONTENT_TYPE_GRADIENTS[code] || 'linear-gradient(135deg, #b2b6c0, #d0d4dc)';
}

// 场景入口 / 分类入口的 icon 字段是后端存的 code（money/book/sofa/robot/bulb/fire/video/dice）
// 统一映射成 emoji，保持前端展示一致
const ENTRY_ICON_MAP: Record<string, string> = {
  money: '💰',
  book: '📚',
  sofa: '🛋️',
  robot: '🤖',
  bulb: '💡',
  fire: '🔥',
  video: '🎬',
  dice: '🎲',
};

export function resolveEntryIcon(icon?: string | null): string {
  if (!icon) return '📁';
  // 如果已经是 emoji（非纯 ascii 字母数字下划线），直接返回
  if (!/^[a-zA-Z0-9_]+$/.test(icon)) return icon;
  return ENTRY_ICON_MAP[icon] || '📁';
}
