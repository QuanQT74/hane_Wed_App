package com.henawedapp.backend.controller;

import com.henawedapp.backend.dto.request.InitiateRegistrationRequest;
import com.henawedapp.backend.dto.request.ResendOtpRequest;
import com.henawedapp.backend.dto.request.VerifyOtpRequest;
import com.henawedapp.backend.dto.response.InitiateRegistrationResponse;
import com.henawedapp.backend.dto.response.ResendOtpResponse;
import com.henawedapp.backend.dto.response.VerifyOtpResponse;
import com.henawedapp.backend.service.RegistrationVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý xác thực OTP trong quá trình đăng ký.
 *
 * API Endpoints:
 * - POST /api/v1/auth/register/initiate  - Bắt đầu đăng ký, gửi OTP
 * - POST /api/v1/auth/register/verify    - Xác thực OTP, tạo tài khoản
 * - POST /api/v1/auth/register/resend-otp - Gửi lại OTP
 */
@RestController
@RequestMapping("/api/v1/auth/register")
@RequiredArgsConstructor
@Slf4j
public class RegistrationVerificationController {

    private final RegistrationVerificationService registrationVerificationService;

    /**
     * Bước 1: Khởi tạo đăng ký.
     * Sau khi submit form, hệ thống tạo PendingRegistration, sinh OTP và gửi cho user.
     *
     * POST /api/v1/auth/register/initiate
     */
    @PostMapping("/initiate")
    public ResponseEntity<InitiateRegistrationResponse> initiateRegistration(
            @Valid @RequestBody InitiateRegistrationRequest request) {

        log.info("Received initiate registration request for contact: {}",
                request.getEmail() != null ? request.getEmail() : request.getPhone());

        InitiateRegistrationResponse response = registrationVerificationService.initiateRegistration(request);

        log.info("Initiate registration successful for pendingRegId: {}",
                response.getPendingRegistrationId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Bước 2: Xác thực OTP.
     * User nhập mã OTP để xác thực. Nếu thành công, tài khoản thực sự được tạo.
     *
     * POST /api/v1/auth/register/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        log.info("Received verify OTP request for contact: {}", request.getContactValue());

        VerifyOtpResponse response = registrationVerificationService.verifyOtp(request);

        log.info("OTP verified and account created for contact: {}", request.getContactValue());

        return ResponseEntity.ok(response);
    }

    /**
     * Bước 3: Gửi lại OTP.
     * User yêu cầu gửi lại mã OTP mới.
     *
     * POST /api/v1/auth/register/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ResendOtpResponse> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        log.info("Received resend OTP request for contact: {}", request.getContactValue());

        ResendOtpResponse response = registrationVerificationService.resendOtp(request);

        log.info("OTP resent successfully for contact: {}", request.getContactValue());

        return ResponseEntity.ok(response);
    }
}
