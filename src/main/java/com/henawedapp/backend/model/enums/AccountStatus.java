package com.henawedapp.backend.model.enums;

/**
 * Trạng thái tài khoản người dùng.
 * PENDING - Chờ kích hoạt (dùng cho legacy)
 * PENDING_APPROVAL - Chờ phê duyệt (sau khi đăng ký với OTP)
 * ACTIVE - Đang hoạt động
 * SUSPENDED - Tạm ngưng
 * BANNED - Bị cấm
 * DELETED - Đã xóa
 */
public enum AccountStatus {
    PENDING,
    PENDING_APPROVAL,
    ACTIVE,
    SUSPENDED,
    BANNED,
    DELETED
}
