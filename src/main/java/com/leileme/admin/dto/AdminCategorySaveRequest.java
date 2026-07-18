package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 管理后台-分类保存请求。
 */
public record AdminCategorySaveRequest(
        String parentCode,

        @NotBlank(message = "分类编码不能为空")
        @Size(max = 50, message = "分类编码最长 50 字")
        String categoryCode,

        @NotBlank(message = "分类名称不能为空")
        @Size(max = 50, message = "分类名称最长 50 字")
        String categoryName,

        String icon,

        @Size(max = 500, message = "描述最长 500 字")
        String description,

        Integer sortNo,

        Integer status
) {}
