package com.leileme.common.response;

public final class RequestIdContext {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private RequestIdContext() {}

    public static void set(String requestId) { HOLDER.set(requestId); }
    public static String get() { return HOLDER.get(); }
    public static void clear() { HOLDER.remove(); }
}
