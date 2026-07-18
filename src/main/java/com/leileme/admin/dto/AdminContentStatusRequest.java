package com.leileme.admin.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 管理后台-内容状态更新请求。
 * status：0=草稿 1=发布 2=下架，发布时自动设置 publishedAt。
 */
public record AdminContentStatusRequest(
        @NotNull(message = "状态不能为空")
        Integer status
) {}
