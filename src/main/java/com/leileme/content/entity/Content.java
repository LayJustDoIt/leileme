package com.leileme.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@TableName("content")
public class Content {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String contentType;
    private Long categoryId;
    private String title;
    private String subtitle;
    private String summary;
    private String body;
    private String searchKeywords;
    private String coverUrl;
    private String sourceName;
    private String sourceUrl;
    private String authorName;
    private Integer status;
    private Integer isOriginal;
    private Integer isTop;
    private BigDecimal hotScore;
    private Long viewCount;
    private Long favoriteCount;
    private Long shareCount;
    private LocalDateTime publishedAt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
