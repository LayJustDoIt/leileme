package com.leileme.admin.vo;

public record AdminLoginResponse(
        String accessToken,
        Long expiresIn,
        AdminUserInfo admin
) {
    public record AdminUserInfo(
            Long id,
            String username,
            String displayName
    ) {}
}
