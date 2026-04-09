package com.henawedapp.backend.exception;

/**
 * Exception thrown when pending registration not found.
 */
public class PendingRegistrationNotFoundException extends RuntimeException {

    public PendingRegistrationNotFoundException(String message) {
        super(message);
    }

    public PendingRegistrationNotFoundException(String contactType, String contactValue) {
        super(String.format("Không tìm thấy đăng ký đang chờ xác thực với %s: %s", contactType, contactValue));
    }
}
