package com.leileme.admin.config;

/**
 * 管理员上下文（ThreadLocal）。由 AdminAuthInterceptor 填充。
 * 与微信用户 UserContext 完全独立，避免混用。
 */
public class AdminUserContext {
    private static final ThreadLocal<AdminJwtUtil.AdminTokenPayload> HOLDER = new ThreadLocal<>();

    public static void set(AdminJwtUtil.AdminTokenPayload payload) {
        HOLDER.set(payload);
    }

    public static AdminJwtUtil.AdminTokenPayload get() {
        return HOLDER.get();
    }

    public static Long getAdminId() {
        AdminJwtUtil.AdminTokenPayload p = HOLDER.get();
        return p == null ? null : p.adminId();
    }

    public static boolean isLoggedIn() {
        return HOLDER.get() != null;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
