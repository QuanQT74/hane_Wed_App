package com.henawedapp.backend.service;

/**
 * Interface cho việc gửi email OTP.
 */
public interface EmailService {

    /**
     * Gửi OTP qua email.
     *
     * @param to      Địa chỉ email người nhận
     * @param otpCode Mã OTP 6 số
     * @param expiryMinutes Thời gian hết hạn (phút)
     */
    void sendOtpEmail(String to, String otpCode, int expiryMinutes);
}
