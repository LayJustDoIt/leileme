package com.leileme.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页推荐位实体。
 */
@Data
@TableName("home_recommendation")
public class HomeRecommendation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String slotCode;
    private Long contentId;
    private Long categoryId;
    private String titleOverride;
    private String imageOverride;
    private Integer sortNo;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
