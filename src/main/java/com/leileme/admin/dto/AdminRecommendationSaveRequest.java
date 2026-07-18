package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 管理后台-首页推荐位保存请求。
 */
public record AdminRecommendationSaveRequest(
        @NotBlank(message = "推荐位编码不能为空")
        @Size(max = 50, message = "推荐位编码最长 50 字")
        String slotCode,

        Long contentId,

        Long categoryId,

        @Size(max = 200, message = "标题覆盖最长 200 字")
        String titleOverride,

        String imageOverride,

        Integer sortNo,

        LocalDateTime startAt,

        LocalDateTime endAt,

        Integer status
) {}
