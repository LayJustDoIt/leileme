package com.leileme.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 50, message = "用户名最长 50 字")
        String username,

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度需在 6 到 100 字之间")
        String password
) {}
