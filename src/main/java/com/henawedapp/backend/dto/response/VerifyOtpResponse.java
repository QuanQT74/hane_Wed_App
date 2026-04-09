package com.henawedapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO phản hồi sau khi xác thực OTP thành công và tài khoản đã được tạo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpResponse {

    /**
     * ID của Account đã tạo.
     */
    private UUID accountId;

    /**
     * ID của MembershipApplication đã tạo.
     */
    private UUID applicationId;

    /**
     * Mã thành viên.
     */
    private String memberCode;

    /**
     * Email của tài khoản.
     */
    private String email;

    /**
     * Số điện thoại của tài khoản.
     */
    private String phone;

    /**
     * Loại hội viên: INDIVIDUAL hoặc ORGANIZATION.
     */
    private String memberType;

    /**
     * Trạng thái tài khoản: PENDING_APPROVAL.
     */
    private String accountStatus;

    /**
     * Thời điểm đăng ký được xác thực.
     */
    private Instant verifiedAt;

    private String message;
}
