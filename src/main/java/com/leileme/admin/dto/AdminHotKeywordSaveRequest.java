package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 管理后台-热门搜索词保存请求。
 */
public record AdminHotKeywordSaveRequest(
        @NotBlank(message = "关键词不能为空")
        @Size(max = 100, message = "关键词最长 100 字")
        String keyword,

        @Size(max = 100, message = "展示名称最长 100 字")
        String displayName,

        String icon,

        Integer sortNo,

        LocalDateTime startAt,

        LocalDateTime endAt,

        Integer status
) {}
