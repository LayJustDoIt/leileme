package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 管理后台-内容保存请求。
 */
public record AdminContentSaveRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 200, message = "标题最长 200 字")
        String title,

        @Size(max = 200, message = "副标题最长 200 字")
        String subtitle,

        @Size(max = 500, message = "摘要最长 500 字")
        String summary,

        String body,

        @Size(max = 200, message = "搜索关键词最长 200 字")
        String searchKeywords,

        String coverUrl,

        @Size(max = 100, message = "来源名称最长 100 字")
        String sourceName,

        String sourceUrl,

        @Size(max = 50, message = "作者名最长 50 字")
        String authorName,

        @NotBlank(message = "内容类型不能为空")
        String contentType,

        Long categoryId,

        @NotNull(message = "状态不能为空")
        Integer status,

        Integer isOriginal,

        Integer isTop,

        java.math.BigDecimal hotScore,

        List<Long> tagIds
) {}
