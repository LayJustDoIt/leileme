package com.leileme.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态拦截器：从 Authorization 头解析 JWT，填充 {@link UserContext}。
 * 本拦截器不阻断请求，未携带或失效的 Token 只是让 UserContext 为空。
 * 需要登录的接口在 Service / Controller 层主动校验 UserContext.isLoggedIn()。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.verify(token);
        if (userId != null) {
            UserContext.set(userId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
