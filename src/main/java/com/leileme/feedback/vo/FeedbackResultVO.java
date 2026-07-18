package com.leileme.feedback.vo;

/**
 * 反馈提交成功后返回。不返回任何用户隐私。
 */
public record FeedbackResultVO(
        long id,
        String submittedAt
) {}
