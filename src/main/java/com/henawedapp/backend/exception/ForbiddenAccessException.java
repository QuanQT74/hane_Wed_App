package com.henawedapp.backend.exception;

/**
 * Exception khi cố gắng truy cập profile không thuộc về mình.
 */
public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException(String message) {
        super(message);
    }

    public ForbiddenAccessException() {
        super("Bạn không có quyền truy cập tài nguyên này.");
    }
}
