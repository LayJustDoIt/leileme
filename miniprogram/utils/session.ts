const SESSION_KEY = 'leileme_session_id';

function randomString(len = 12): string {
  const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < len; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

// 全局复用同一个 sessionId，用于匿名行为记录
export function ensureSessionId(): string {
  let sid = '';
  try {
    sid = wx.getStorageSync(SESSION_KEY) || '';
  } catch (e) {
    sid = '';
  }
  if (!sid) {
    sid = `mp-${Date.now()}-${randomString()}`;
    try {
      wx.setStorageSync(SESSION_KEY, sid);
    } catch (e) {
      // 写入失败仍可继续，下次重新生成
    }
  }
  return sid;
}

export function getSessionId(): string {
  const app = getApp<IAppOption>();
  return app?.globalData?.sessionId || ensureSessionId();
}
