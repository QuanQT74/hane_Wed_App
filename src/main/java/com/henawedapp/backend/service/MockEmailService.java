package com.henawedapp.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock implementation của EmailService.
 * Trong môi trường production, thay thế bằng implementation thực sự (Spring Mail, SendGrid, etc.)
 */
@Service
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendOtpEmail(String to, String otpCode, int expiryMinutes) {
        log.info("========== MOCK EMAIL ==========");
        log.info("To: {}", to);
        log.info("OTP: {}", otpCode);
        log.info("Expires in: {} minutes", expiryMinutes);
        log.info("================================");
    }
}
