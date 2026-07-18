package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 管理后台-反馈状态更新请求。
 * status：PENDING/PROCESSED/IGNORED。
 */
public record AdminFeedbackStatusRequest(
        @NotBlank(message = "状态不能为空")
        @Pattern(regexp = "PENDING|PROCESSED|IGNORED", message = "状态仅支持 PENDING/PROCESSED/IGNORED")
        String status
) {}
