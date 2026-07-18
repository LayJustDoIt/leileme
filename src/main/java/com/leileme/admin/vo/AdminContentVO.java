package com.leileme.admin.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台-内容详情视图。
 */
public record AdminContentVO(
        Long id,
        String title,
        String subtitle,
        String summary,
        String body,
        String searchKeywords,
        String coverUrl,
        String sourceName,
        String sourceUrl,
        String authorName,
        String contentType,
        Long categoryId,
        Integer status,
        Integer isOriginal,
        Integer isTop,
        BigDecimal hotScore,
        Long viewCount,
        Long favoriteCount,
        Long shareCount,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Long> tagIds
) {}
