package com.henawedapp.backend.model.enums;

/**
 * Trạng thái của OTP.
 * ACTIVE - Đang hoạt động, có thể sử dụng
 * USED - Đã được sử dụng để xác thực
 * EXPIRED - Đã hết hạn
 * MAX_ATTEMPTS_EXCEEDED - Đã vượt số lần thử
 */
public enum OtpStatus {
    ACTIVE,
    USED,
    EXPIRED,
    MAX_ATTEMPTS_EXCEEDED
}
