package com.leileme.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与校验工具。使用 HMAC-SHA256 对称签名。
 * Token payload 中保存 userId（subject），有效期 7 天。
 */
@Component
public class JwtUtil {
    private final AuthProperties authProperties;

    public JwtUtil(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    private SecretKey signingKey() {
        String secret = authProperties.getJwt().getSecret();
        if (secret == null || secret.length() < 32) {
            // 兜底：开发环境未配置时用固定值，避免启动报错；生产必须通过环境变量覆盖
            secret = "leileme-dev-jwt-secret-do-not-use-in-prod-32bytes!";
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 Token。
     * @param userId 用户 ID
     * @return JWT 字符串
     */
    public String issue(Long userId) {
        long now = System.currentTimeMillis();
        long expiresInMillis = authProperties.getJwt().getExpiresIn() * 1000L;
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiresInMillis))
                .signWith(signingKey())
                .compact();
    }

    /**
     * 校验并解析 Token，返回 userId。失败返回 null。
     */
    public Long verify(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String sub = claims.getSubject();
            return sub == null ? null : Long.valueOf(sub);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 从 Authorization 头提取 token（去掉 "Bearer " 前缀）。
     */
    public static String extractToken(String authHeader) {
        if (authHeader == null) return null;
        String trimmed = authHeader.trim();
        if (trimmed.toLowerCase().startsWith("bearer ")) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}
