package com.leileme.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("search_click_record")
public class SearchClickRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestId;
    private Long userId;
    private String sessionId;
    private Long contentId;
    private Integer resultPosition;
    private LocalDateTime createdAt;
}
