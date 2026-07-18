package com.leileme.admin.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 管理后台-推荐位启停请求。
 * status：1=启用 0=禁用。
 */
public record AdminRecommendationStatusRequest(
        @NotNull(message = "状态不能为空")
        Integer status
) {}
