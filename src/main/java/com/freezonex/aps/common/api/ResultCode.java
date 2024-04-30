package com.freezonex.aps.common.api;

/**
 * 枚举了一些常用API操作码
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "Successful operation"),
    FAILED(500, "The operation failed, please contact the administrator!"),
    VALIDATE_FAILED(404, "Parameter test failure"),
    UNAUTHORIZED(401, "Not logged in or token, it has expired"),
    FORBIDDEN(403, "No relevant authority");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
