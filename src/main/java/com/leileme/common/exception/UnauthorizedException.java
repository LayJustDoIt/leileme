package com.leileme.common.exception;

/**
 * 未登录或登录态失效。映射到 HTTP 401。
 */
public class UnauthorizedException extends RuntimeException {
    private final int code;

    public UnauthorizedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public UnauthorizedException(String message) {
        this(40100, message);
    }

    public int getCode() { return code; }
}
