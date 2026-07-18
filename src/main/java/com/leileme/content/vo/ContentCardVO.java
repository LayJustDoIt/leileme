package com.leileme.content.vo;

import java.time.LocalDateTime;
import java.util.List;

public record ContentCardVO(
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
        boolean favorite
) {}
