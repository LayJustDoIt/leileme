package com.leileme.admin.vo;

import java.time.LocalDateTime;

/**
 * 管理后台-反馈记录详情视图。
 */
public record AdminFeedbackVO(
        Long id,
        Long userId,
        String sessionId,
        String content,
        String contact,
        String pagePath,
        LocalDateTime submittedAt,
        LocalDateTime createdAt,
        String status,
        String adminNote,
        Long handledBy,
        LocalDateTime handledAt
) {}
