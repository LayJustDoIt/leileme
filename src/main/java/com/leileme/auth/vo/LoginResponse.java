package com.leileme.auth.vo;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        UserVO user
) {}
