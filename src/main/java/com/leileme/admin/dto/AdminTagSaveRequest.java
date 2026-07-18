package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 管理后台-标签保存请求。
 */
public record AdminTagSaveRequest(
        @NotBlank(message = "标签名称不能为空")
        @Size(max = 50, message = "标签名称最长 50 字")
        String tagName,

        @NotBlank(message = "标签编码不能为空")
        @Size(max = 50, message = "标签编码最长 50 字")
        String tagCode,

        Integer status
) {}
