/// <reference path="./types/wechat-miniprogram/index.d.ts" />

declare const wx: WechatMiniprogram.Wx;
declare const App: WechatMiniprogram.App.Constructor;
declare const Page: WechatMiniprogram.Page.Constructor;
declare const Component: WechatMiniprogram.Component.Constructor;
declare const getApp: <T = any>() => T & IAppOption;
declare const getCurrentPages: () => Array<WechatMiniprogram.Page.Instance<any, any>>;

// 微信小程序运行时支持 setTimeout / setInterval / clearTimeout / clearInterval，
// 但官方 wechat-miniprogram 类型包未声明，这里补齐以便 TypeScript 检查通过。
declare const setTimeout: (handler: (...args: any[]) => void, timeout: number, ...args: any[]) => number;
declare const clearTimeout: (id?: number) => void;
declare const setInterval: (handler: (...args: any[]) => void, timeout: number, ...args: any[]) => number;
declare const clearInterval: (id?: number) => void;
