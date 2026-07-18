package com.leileme.admin.dto;

import jakarta.validation.constraints.Size;

/**
 * 管理后台-公共配置更新请求。
 */
public record AdminPublicConfigUpdateRequest(
        @Size(max = 50, message = "应用名称最长 50 字")
        String appName,

        @Size(max = 100, message = "标语最长 100 字")
        String slogan,

        @Size(max = 20, message = "版本号最长 20 字")
        String versionName,

        @Size(max = 100, message = "关于标题最长 100 字")
        String aboutTitle,

        String aboutContent,

        Boolean feedbackEnabled,

        @Size(max = 200, message = "反馈提示最长 200 字")
        String feedbackHint,

        String contactText,

        String announcement
) {}
