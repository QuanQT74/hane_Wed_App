package com.henawedapp.backend.model.enums;

/**
 * Trạng thái tài khoản người dùng.
 * PENDING - Chờ kích hoạt
 * ACTIVE - Đang hoạt động
 * SUSPENDED - Tạm ngưng
 * BANNED - Bị cấm
 * DELETED - Đã xóa
 */
public enum AccountStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    BANNED,
    DELETED
}
