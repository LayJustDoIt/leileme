package com.leileme.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leileme.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 管理员鉴权拦截器：阻断式。未携带或失效的管理员 Token 直接返回 401。
 * 仅拦截 /api/admin/v1/**（登录接口本身通过 excludePathPatterns 排除）。
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    private final AdminJwtUtil adminJwtUtil;
    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(AdminJwtUtil adminJwtUtil, ObjectMapper objectMapper) {
        this.adminJwtUtil = adminJwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        String token = extractToken(authHeader);
        AdminJwtUtil.AdminTokenPayload payload = adminJwtUtil.verify(token);
        if (payload == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            ApiResponse<Void> body = ApiResponse.error(40101, "管理员未登录或登录已过期");
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return false;
        }
        AdminUserContext.set(payload);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AdminUserContext.clear();
    }

    private static String extractToken(String authHeader) {
        if (authHeader == null) return null;
        String trimmed = authHeader.trim();
        if (trimmed.toLowerCase().startsWith("bearer ")) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }
}
