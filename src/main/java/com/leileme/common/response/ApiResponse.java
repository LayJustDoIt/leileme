package com.leileme.common.response;

public record ApiResponse<T>(int code, String message, T data, String requestId) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data, RequestIdContext.get());
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, RequestIdContext.get());
    }
}
