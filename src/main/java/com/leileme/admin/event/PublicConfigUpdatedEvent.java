package com.leileme.admin.event;

/**
 * 公共配置更新事件。
 * 小程序端的 public-config 缓存清理暂不实现（小程序端 30 分钟自动过期）。
 */
public class PublicConfigUpdatedEvent {
    private final String appName;
    private final String versionName;

    public PublicConfigUpdatedEvent(String appName, String versionName) {
        this.appName = appName;
        this.versionName = versionName;
    }

    public String getAppName() { return appName; }
    public String getVersionName() { return versionName; }
}
