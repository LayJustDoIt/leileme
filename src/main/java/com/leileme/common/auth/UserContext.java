package com.leileme.common.auth;

/**
 * 当前请求登录用户上下文（ThreadLocal）。
 * 由 {@link AuthInterceptor} 在请求进入时填充，请求结束时清理。
 */
public final class UserContext {
    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    private UserContext() {}

    public static void set(Long userId) { HOLDER.set(userId); }
    public static Long get() { return HOLDER.get(); }
    public static boolean isLoggedIn() { return HOLDER.get() != null; }
    public static void clear() { HOLDER.remove(); }
}
