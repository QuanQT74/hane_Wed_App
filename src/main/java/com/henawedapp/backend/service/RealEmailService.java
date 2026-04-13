package com.henawedapp.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation thực của EmailService sử dụng Spring Mail.
 * Gửi OTP qua email thật sự.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
@Async
public class RealEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otpCode, int expiryMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Mã xác thực OTP - Hane Web App");

            String htmlContent = buildEmailContent(otpCode, expiryMinutes);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("OTP email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.", e);
        }
    }

    private String buildEmailContent(String otpCode, int expiryMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { background-color: #ffffff; border-radius: 10px; padding: 30px; max-width: 500px; margin: 0 auto; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; color: #333; }
                    .otp-code { font-size: 36px; font-weight: bold; text-align: center; color: #007bff; margin: 30px 0; letter-spacing: 10px; }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Xác thực mã OTP</h2>
                    </div>
                    <p>Xin chào,</p>
                    <p>Mã xác thực của bạn là:</p>
                    <div class="otp-code">%s</div>
                    <p>Mã này có hiệu lực trong <strong>%d phút</strong>.</p>
                    <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>
                    <div class="footer">
                        <p>Hệ thống Hane Web App</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otpCode, expiryMinutes);
    }
}
