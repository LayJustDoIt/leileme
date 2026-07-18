package com.leileme.history.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("user_browse_record")
public class UserBrowseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String sessionId;
    private Long contentId;
    private Integer browseCount;
    private LocalDateTime firstBrowsedAt;
    private LocalDateTime lastBrowsedAt;
}
