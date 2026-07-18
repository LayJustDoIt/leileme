package com.leileme.common.config.vo;

/**
 * 公共配置对外响应。不包含任何密钥。
 */
public record PublicConfigVO(
        String appName,
        String slogan,
        String versionName,
        String aboutTitle,
        String aboutContent,
        boolean feedbackEnabled,
        String feedbackHint,
        String contactText,
        String announcement
) {}
