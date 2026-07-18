package com.leileme.content.vo;

import java.time.LocalDateTime;
import java.util.List;

public record ContentDetailVO(
        Long id,
        String contentType,
        String title,
        String summary,
        String body,
        String coverUrl,
        String sourceName,
        String sourceUrl,
        String authorName,
        List<String> tags,
        Long viewCount,
        Long favoriteCount,
        Long shareCount,
        LocalDateTime publishedAt,
        boolean favorite,
        List<ContentCardVO> relatedContents
) {}
