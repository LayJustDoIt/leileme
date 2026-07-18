import { ensureSessionId } from './utils/session';

interface AppGlobalData {
  sessionId: string;
  systemInfo?: WechatMiniprogram.Wx.SystemInfo;
}

App<AppGlobalData>({
  globalData: {
    sessionId: '',
  },
  onLaunch() {
    this.globalData.sessionId = ensureSessionId();
    try {
      this.globalData.systemInfo = wx.getSystemInfoSync();
    } catch (e) {
      // 忽略系统信息获取失败
    }
  },
});
