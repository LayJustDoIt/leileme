package com.leileme.home.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("hot_keyword")
public class HotKeyword {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keyword;
    private String displayName;
    private String icon;
    private Integer sortNo;
    private Long clickCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
