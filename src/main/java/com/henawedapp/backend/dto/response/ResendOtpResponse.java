package com.henawedapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO phản hồi sau khi yêu cầu gửi lại OTP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResendOtpResponse {

    /**
     * Kênh OTP đã được gửi: EMAIL hoặc PHONE.
     */
    private String otpChannel;

    /**
     * Thời điểm OTP mới hết hạn.
     */
    private Instant otpExpiresAt;

    /**
     * Số lần resend còn lại.
     */
    private Integer remainingResendCount;

    private String message;
}
