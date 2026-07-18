package com.leileme.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("search_record")
public class SearchRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestId;
    private Long userId;
    private String sessionId;
    private String originalKeyword;
    private String normalizedKeyword;
    private String contentType;
    private Long categoryId;
    private Integer resultCount;
    private Integer responseTimeMs;
    private LocalDateTime createdAt;
}
