package com.henawedapp.backend.model.enums;

/**
 * Trạng thái của đăng ký tạm.
 * PENDING - Đang chờ xác thực OTP
 * VERIFIED - Đã xác thực thành công
 * EXPIRED - Đã hết hạn
 * CANCELLED - Đã bị hủy
 */
public enum PendingRegistrationStatus {
    PENDING,
    VERIFIED,
    EXPIRED,
    CANCELLED
}
