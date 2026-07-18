package com.leileme.admin.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 管理员 JWT 工具。与微信用户 JwtUtil 分开：
 * - 使用独立的 admin secret
 * - claim 中加入 type=admin 标识，防止用户 Token 误进入管理后台
 */
@Component
public class AdminJwtUtil {
    private final AdminAuthProperties properties;

    public AdminJwtUtil(AdminAuthProperties properties) {
        this.properties = properties;
    }

    private SecretKey signingKey() {
        String secret = properties.getJwt().getSecret();
        if (secret == null || secret.length() < 32) {
            secret = "leileme-admin-jwt-secret-do-not-use-in-prod-32bytes!";
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发管理员 Token。
     */
    public String issue(Long adminId, String username) {
        long now = System.currentTimeMillis();
        long expiresInMillis = properties.getJwt().getExpiresIn() * 1000L;
        return Jwts.builder()
                .subject(String.valueOf(adminId))
                .claim("type", "admin")
                .claim("username", username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiresInMillis))
                .signWith(signingKey())
                .compact();
    }

    /**
     * 校验并解析管理员 Token。失败或非 admin 类型返回 null。
     */
    public AdminTokenPayload verify(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String type = claims.get("type", String.class);
            if (!"admin".equals(type)) return null;
            String sub = claims.getSubject();
            String username = claims.get("username", String.class);
            if (sub == null) return null;
            return new AdminTokenPayload(Long.valueOf(sub), username);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public record AdminTokenPayload(Long adminId, String username) {}
}
