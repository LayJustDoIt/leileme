package com.leileme.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容标签实体。
 */
@Data
@TableName("content_tag")
public class ContentTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tagName;
    private String tagCode;
    private Long useCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
