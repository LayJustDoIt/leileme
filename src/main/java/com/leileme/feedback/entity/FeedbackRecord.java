package com.leileme.feedback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback_record")
public class FeedbackRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String sessionId;
    private String content;
    private String contact;
    private String pagePath;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    // 管理后台扩展字段
    private String status;
    private String adminNote;
    private Long handledBy;
    private LocalDateTime handledAt;
}
