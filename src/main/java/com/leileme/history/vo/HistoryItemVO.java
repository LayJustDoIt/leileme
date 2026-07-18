package com.leileme.history.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 浏览历史列表项：内容卡片信息 + 浏览元数据。
 */
public record HistoryItemVO(
        Long id,
        String contentType,
        String title,
        String summary,
        String coverUrl,
        String sourceName,
        List<String> tags,
        Long viewCount,
        Long favoriteCount,
        LocalDateTime publishedAt,
        boolean favorite,
        Integer browseCount,
        LocalDateTime lastBrowsedAt
) {}
