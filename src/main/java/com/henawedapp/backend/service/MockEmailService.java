package com.henawedapp.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Mock implementation của EmailService.
 * Chỉ active khi profile = "mock"
 */
@Service
@Profile("mock")
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
