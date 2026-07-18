// 精简版微信小程序类型声明，覆盖项目所用 API。
// 完整类型可通过 npm i -D miniprogram-api-typings 引入。

declare namespace WechatMiniprogram {
  namespace Wx {
    interface RequestOption {
      url: string;
      data?: string | AnyObject | ArrayBuffer;
      method?: 'OPTIONS' | 'GET' | 'HEAD' | 'POST' | 'PUT' | 'DELETE' | 'TRACE' | 'CONNECT';
      header?: Record<string, string>;
      timeout?: number;
      success?: (res: RequestSuccessCallbackResult) => void;
      fail?: (err: GeneralCallbackResult) => void;
      complete?: (res: RequestSuccessCallbackResult | GeneralCallbackResult) => void;
    }
    interface RequestSuccessCallbackResult {
      data: string | AnyObject | ArrayBuffer;
      statusCode: number;
      header: Record<string, string>;
      errMsg: string;
      cookies?: Array<{ name: string; value: string; expires?: string; path?: string }>;
      profile?: AnyObject;
    }
    interface GeneralCallbackResult {
      errMsg: string;
    }
    interface ShowToastOption {
      title: string;
      icon?: 'success' | 'loading' | 'error' | 'none';
      image?: string;
      duration?: number;
      mask?: boolean;
      success?: () => void;
      fail?: () => void;
      complete?: () => void;
    }
    interface ShowLoadingOption {
      title: string;
      mask?: boolean;
      success?: () => void;
      fail?: () => void;
      complete?: () => void;
    }
    interface NavigateToOption {
      url: string;
      success?: () => void;
      fail?: (err: GeneralCallbackResult) => void;
      complete?: () => void;
      events?: AnyObject;
    }
    interface SetStorageOption {
      key: string;
      data: any;
    }
    interface GetStorageOption {
      key: string;
      success?: (res: { data: any }) => void;
      fail?: (err: GeneralCallbackResult) => void;
    }
    interface RequestTask {
      abort: () => void;
      onChunkReceived?: (cb: (res: { data: ArrayBuffer }) => void) => void;
      offChunkReceived?: (cb: (res: { data: ArrayBuffer }) => void) => void;
    }
    interface SystemInfo {
      statusBarHeight?: number;
      windowWidth: number;
      windowHeight: number;
      platform: string;
      SDKVersion: string;
    }
  }

  interface BaseEvent {
    type: string;
    currentTarget: { id: string; dataset: AnyObject };
    target: { id: string; dataset: AnyObject };
    detail: AnyObject;
    timeStamp: number;
  }
  interface TouchEvent extends BaseEvent {
    touches: Array<{ clientX: number; clientY: number; identifier: number; pageX: number; pageY: number }>;
    changedTouches: Array<{ clientX: number; clientY: number; identifier: number; pageX: number; pageY: number }>;
  }
  interface CustomEvent<T = AnyObject> extends BaseEvent {
    detail: T;
  }

  interface Wx {
    request(option: Wx.RequestOption): Wx.RequestTask;
    showToast(option: Wx.ShowToastOption): void;
    showLoading(option: Wx.ShowLoadingOption): void;
    hideLoading(): void;
    showModal(option: {
      title?: string;
      content?: string;
      showCancel?: boolean;
      cancelText?: string;
      confirmText?: string;
      confirmColor?: string;
      success?: (res: { confirm: boolean; cancel: boolean }) => void;
    }): void;
    navigateTo(option: Wx.NavigateToOption): void;
    navigateBack(option?: { delta?: number }): void;
    switchTab(option: { url: string; success?: () => void; fail?: (err: Wx.GeneralCallbackResult) => void }): void;
    setStorageSync(key: string, data: any): void;
    getStorageSync(key: string): any;
    removeStorageSync(key: string): void;
    setStorage(option: Wx.SetStorageOption): void;
    getStorage(option: Wx.GetStorageOption): void;
    getSystemInfoSync(): Wx.SystemInfo;
    getSystemInfo(option: { success?: (res: Wx.SystemInfo) => void }): void;
    setNavigationBarTitle(option: { title: string }): void;
    stopPullDownRefresh(): void;
    showShareMenu(option: { withShareTicket?: boolean; menus?: string[] }): void;
    vibrateShort(option?: { type?: 'heavy' | 'medium' | 'light' }): void;
  }

  namespace App {
    interface Instance<T extends AnyObject = AnyObject> {
      globalData: T;
      [key: string]: any;
    }
    interface Constructor {
      <T extends AnyObject = AnyObject>(options: {
        onLaunch?: (options?: AnyObject) => void;
        onShow?: (options?: AnyObject) => void;
        onHide?: () => void;
        onError?: (err: string) => void;
        globalData: T;
      } & ThisType<Instance<T>>): void;
    }
  }

  namespace Page {
    interface Instance<D, M> {
      data: D;
      setData: (data: Partial<D> & AnyObject, callback?: () => void) => void;
      [key: string]: any;
    }
    interface Constructor {
      <D extends AnyObject = {}, M extends AnyObject = {}>(options: {
        data: D;
        onLoad?: (query: AnyObject) => void | Promise<void>;
        onShow?: () => void;
        onReady?: () => void;
        onHide?: () => void;
        onUnload?: () => void;
        onPullDownRefresh?: () => void;
        onReachBottom?: () => void;
        onShareAppMessage?: (res?: AnyObject) => { title?: string; path?: string; imageUrl?: string } | false;
        onShareTimeline?: () => { title?: string; query?: string; imageUrl?: string } | false;
        [key: string]: any;
      } & M & ThisType<Instance<D, M>>): void;
    }
  }

  namespace Component {
    interface Instance {
      data: AnyObject;
      setData: (data: AnyObject, callback?: () => void) => void;
      triggerEvent: (name: string, detail?: AnyObject, options?: AnyObject) => void;
      createSelectorQuery: () => AnyObject;
      [key: string]: any;
    }
    interface Constructor {
      (options: {
        options?: { multipleSlots?: boolean; addGlobalClass?: boolean; styleIsolation?: 'isolated' | 'apply-shared' | 'shared' };
        properties?: Record<string, AnyObject>;
        data?: AnyObject;
        observers?: Record<string, (...args: any[]) => void>;
        lifetimes?: {
          created?: () => void;
          attached?: () => void;
          ready?: () => void;
          moved?: () => void;
          detached?: () => void;
        };
        pageLifetimes?: {
          show?: () => void;
          hide?: () => void;
          resize?: (size: { size: { windowWidth: number; windowHeight: number } }) => void;
        };
        methods?: AnyObject;
        [key: string]: any;
      } & ThisType<Instance>): void;
    }
  }
}

interface AnyObject {
  [key: string]: any;
}

interface IAppOption {
  globalData: {
    sessionId: string;
    systemInfo?: WechatMiniprogram.Wx.SystemInfo;
  };
}

// 小程序运行时内置 console，但 lib: ["ES2020"] 不含 DOM，需手动声明
declare const console: {
  log(...args: any[]): void;
  info(...args: any[]): void;
  warn(...args: any[]): void;
  error(...args: any[]): void;
  debug(...args: any[]): void;
};
