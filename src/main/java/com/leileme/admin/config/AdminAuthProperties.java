package com.leileme.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 管理员鉴权配置。管理员 Token 与微信用户 Token 分开：
 * - 不同的 JWT secret（admin-jwt.secret）
 * - 不同的 claim type（type=admin）
 * - 不同的拦截路径（/api/admin/v1/**）
 */
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminAuthProperties {
    private Jwt jwt = new Jwt();
    private Bootstrap bootstrap = new Bootstrap();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Bootstrap getBootstrap() { return bootstrap; }
    public void setBootstrap(Bootstrap bootstrap) { this.bootstrap = bootstrap; }

    public static class Jwt {
        private String secret = "${ADMIN_JWT_SECRET:leileme-admin-jwt-secret-change-me-2026-min32bytes!!}";
        private long expiresIn = 86400;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    }

    public static class Bootstrap {
        private boolean enabled = true;
        private String username = "admin";
        private String password = "admin123";
        private String displayName = "超级管理员";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
    }
}
