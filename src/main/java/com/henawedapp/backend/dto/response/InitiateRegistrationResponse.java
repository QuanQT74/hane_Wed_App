package com.henawedapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO phản hồi sau khi bắt đầu đăng ký (initiate registration).
 * Cho biết OTP đã được gửi thành công.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiateRegistrationResponse {

    /**
     * ID của PendingRegistration để dùng trong các bước tiếp theo.
     */
    private String pendingRegistrationId;

    /**
     * Kênh OTP đã được gửi: EMAIL hoặc PHONE.
     */
    private String otpChannel;

    /**
     * Thời điểm OTP hết hạn.
     */
    private Instant otpExpiresAt;

    /**
     * Thời điểm đăng ký tạm hết hạn.
     */
    private Instant registrationExpiresAt;

    /**
     * Số lần resend còn lại (tối đa 3 lần).
     */
    private Integer remainingResendCount;

    private String message;
}
