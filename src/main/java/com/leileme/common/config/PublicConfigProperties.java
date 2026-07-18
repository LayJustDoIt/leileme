package com.leileme.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 公共配置（对外可读，不含任何密钥）。
 *
 * 通过 application.yml 中 public-config 节点配置；未配置时使用代码内默认值。
 * 严禁把 AppSecret / JWT Secret / 数据库密码等敏感字段加入本配置。
 */
@Component
@ConfigurationProperties(prefix = "public-config")
public class PublicConfigProperties {
    private String appName = "累了么";
    private String slogan = "你的碎片时间搭子";
    private String versionName = "1.0.0";
    private String aboutTitle = "关于累了么";
    private String aboutContent = "累了么是一个面向碎片时间的内容发现工具，帮助用户发现热点、实用工具、创作灵感和轻量内容。";
    private boolean feedbackEnabled = true;
    private String feedbackHint = "告诉我们你的想法";
    private String contactText = "";
    private String announcement = "";

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getSlogan() { return slogan; }
    public void setSlogan(String slogan) { this.slogan = slogan; }
    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }
    public String getAboutTitle() { return aboutTitle; }
    public void setAboutTitle(String aboutTitle) { this.aboutTitle = aboutTitle; }
    public String getAboutContent() { return aboutContent; }
    public void setAboutContent(String aboutContent) { this.aboutContent = aboutContent; }
    public boolean isFeedbackEnabled() { return feedbackEnabled; }
    public void setFeedbackEnabled(boolean feedbackEnabled) { this.feedbackEnabled = feedbackEnabled; }
    public String getFeedbackHint() { return feedbackHint; }
    public void setFeedbackHint(String feedbackHint) { this.feedbackHint = feedbackHint; }
    public String getContactText() { return contactText; }
    public void setContactText(String contactText) { this.contactText = contactText; }
    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }
}
