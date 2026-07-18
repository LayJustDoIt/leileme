package com.leileme.admin.dto;

import jakarta.validation.constraints.Size;

/**
 * 管理后台-反馈管理员备注请求。
 */
public record AdminFeedbackNoteRequest(
        @Size(max = 1000, message = "备注最长 1000 字")
        String adminNote
) {}
