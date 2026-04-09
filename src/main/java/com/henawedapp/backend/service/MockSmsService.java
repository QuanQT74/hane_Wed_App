package com.henawedapp.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock implementation của SmsService.
 * Trong môi trường production, thay thế bằng implementation thực sự (Twilio, VNPT, etc.)
 */
@Service
@Slf4j
public class MockSmsService implements SmsService {

    @Override
    public void sendOtpSms(String phoneNumber, String otpCode, int expiryMinutes) {
        log.info("========== MOCK SMS ==========");
        log.info("Phone: {}", phoneNumber);
        log.info("OTP: {}", otpCode);
        log.info("Expires in: {} minutes", expiryMinutes);
        log.info("==============================");
    }
}
