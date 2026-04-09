package com.henawedapp.backend.service;

/**
 * Interface cho việc gửi SMS OTP.
 */
public interface SmsService {

    /**
     * Gửi OTP qua SMS.
     *
     * @param phoneNumber Số điện thoại người nhận
     * @param otpCode    Mã OTP 6 số
     * @param expiryMinutes Thời gian hết hạn (phút)
     */
    void sendOtpSms(String phoneNumber, String otpCode, int expiryMinutes);
}
