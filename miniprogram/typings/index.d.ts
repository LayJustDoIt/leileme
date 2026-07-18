/// <reference path="./types/wechat-miniprogram/index.d.ts" />

declare const wx: WechatMiniprogram.Wx;
declare const App: WechatMiniprogram.App.Constructor;
declare const Page: WechatMiniprogram.Page.Constructor;
declare const Component: WechatMiniprogram.Component.Constructor;
declare const getApp: <T = any>() => T & IAppOption;
declare const getCurrentPages: () => Array<WechatMiniprogram.Page.Instance<any, any>>;
