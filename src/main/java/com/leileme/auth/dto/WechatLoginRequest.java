package com.leileme.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record WechatLoginRequest(
        @NotBlank String code,
        String sessionId
) {}
