package com.leileme.category.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("content_category")
public class ContentCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String categoryCode;
    private String categoryName;
    private String icon;
    private String description;
    private Integer sortNo;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
