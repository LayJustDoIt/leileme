import { submitFeedback } from '../../services/api';
import { getCachedPublicConfig } from '../../services/public-config';
import { BizError, NetError } from '../../utils/request';
import { getSessionId } from '../../utils/session';
import type { PublicConfigVO } from '../../types/api';

interface FeedbackPageData {
  content: string;
  contact: string;
  pagePath: string;
  submitting: boolean;
  error: string;
  contentMax: number;
  contactMax: number;
  contentMin: number;
  config: PublicConfigVO | null;
}

const CONTENT_MIN = 5;
const CONTENT_MAX = 1000;
const CONTACT_MAX = 100;
// 客户端防连点：成功后 3 秒内不允许再次提交相同内容（体验优化，不代替后端防重复）
const SAME_CONTENT_COOLDOWN_MS = 3000;

// 模块级变量：上次成功提交的时间戳与内容，用于客户端 3 秒冷却
let lastSubmitAt = 0;
let lastSubmittedContent = '';

Page<FeedbackPageData, {}>({
  data: {
    content: '',
    contact: '',
    pagePath: 'pages/feedback/feedback',
    submitting: false,
    error: '',
    contentMax: CONTENT_MAX,
    contactMax: CONTACT_MAX,
    contentMin: CONTENT_MIN,
    config: null,
  },

  onLoad() {
    // 同步读取缓存配置用于初始展示（如反馈引导文案），后台异步刷新
    const cached = getCachedPublicConfig();
    this.setData({ config: cached });
    // 反馈功能未启用时直接引导回退
    if (cached && cached.feedbackEnabled === false) {
      wx.showModal({
        title: '提示',
        content: cached.feedbackHint || '反馈功能暂未开放',
        showCancel: false,
        confirmText: '返回',
        success: () => {
          wx.navigateBack();
        },
      });
    }
  },

  onContentInput(e: WechatMiniprogram.CustomEvent<{ value: string }>) {
    const value = e.detail.value || '';
    this.setData({ content: value, error: '' });
  },

  onContactInput(e: WechatMiniprogram.CustomEvent<{ value: string }>) {
    const value = e.detail.value || '';
    this.setData({ contact: value });
  },

  async onSubmit() {
    // 请求中禁止重复点击
    if (this.data.submitting) return;

    const content = (this.data.content || '').trim();
    if (content.length < CONTENT_MIN) {
      this.setData({ error: `反馈内容至少 ${CONTENT_MIN} 个字` });
      return;
    }
    if (content.length > CONTENT_MAX) {
      this.setData({ error: `反馈内容最多 ${CONTENT_MAX} 个字` });
      return;
    }
    const contact = (this.data.contact || '').trim();
    if (contact.length > CONTACT_MAX) {
      this.setData({ error: `联系方式最多 ${CONTACT_MAX} 个字` });
      return;
    }

    // 客户端 3 秒冷却：相同内容提交过后未过冷却期
    const now = Date.now();
    if (
      lastSubmitAt > 0 &&
      now - lastSubmitAt < SAME_CONTENT_COOLDOWN_MS &&
      lastSubmittedContent === content
    ) {
      this.setData({ error: '这条反馈刚刚已经提交，请不要重复提交' });
      return;
    }

    // 每次提交前清空旧错误并锁定按钮
    this.setData({ error: '', submitting: true });
    try {
      await submitFeedback({
        content,
        contact: contact || undefined,
        pagePath: this.data.pagePath,
      });
      // 成功：记录时间与内容，清空输入，延迟返回
      lastSubmitAt = Date.now();
      lastSubmittedContent = content;
      this.setData({ content: '', contact: '', error: '' });
      wx.showToast({ title: '反馈提交成功', icon: 'success', duration: 1500 });
      setTimeout(() => {
        wx.navigateBack();
      }, 1200);
    } catch (e: any) {
      // 调试日志：statusCode / code / message / sessionId / content 长度
      // 不打印 Authorization Token 与联系方式完整内容
      const sid = getSessionId();
      if (e instanceof BizError) {
        console.warn('[feedback] BizError', {
          statusCode: e.statusCode,
          code: e.code,
          message: e.message,
          sessionId: sid,
          contentLength: content.length,
        });
        if (e.code === 42901) {
          this.setData({ error: '这条反馈刚刚已经提交，请不要重复提交' });
        } else {
          this.setData({ error: e.message || '提交失败，请稍后再试' });
        }
      } else if (e instanceof NetError) {
        console.warn('[feedback] NetError', {
          message: e.message,
          sessionId: sid,
          contentLength: content.length,
        });
        this.setData({ error: '网络连接失败，请稍后再试' });
      } else {
        console.warn('[feedback] UnknownError', {
          name: e?.name,
          message: e?.message,
          sessionId: sid,
          contentLength: content.length,
        });
        this.setData({ error: '提交失败，请稍后再试' });
      }
    } finally {
      this.setData({ submitting: false });
    }
  },

  onShareAppMessage() {
    return {
      title: '累了么 · 反馈建议',
      path: '/pages/index/index',
    };
  },
});
