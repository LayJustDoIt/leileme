package com.leileme.search.mapper.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SearchContentRow {
    private Long id;
    private String contentType;
    private Long categoryId;
    private String title;
    private String summary;
    private String coverUrl;
    private String sourceName;
    private BigDecimal hotScore;
    private Long viewCount;
    private Long favoriteCount;
    private LocalDateTime publishedAt;
    private BigDecimal relevanceScore;
}
