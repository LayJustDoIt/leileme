package com.leileme.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户反馈提交请求。
 * content 必填，长度 5-1000；
 * contact 可选，最大 100；
 * pagePath 可选，建议填写；
 * sessionId 可选，未登录时用于标识匿名用户。
 */
public record FeedbackRequest(
        @NotBlank(message = "反馈内容不能为空")
        @Size(min = 5, max = 1000, message = "反馈内容长度需在 5 到 1000 字之间")
        String content,

        @Size(max = 100, message = "联系方式最长 100 字")
        String contact,

        String pagePath,

        String sessionId
) {}
