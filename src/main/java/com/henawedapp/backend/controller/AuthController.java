package com.henawedapp.backend.controller;

import com.henawedapp.backend.dto.request.LoginRequest;
import com.henawedapp.backend.dto.request.RefreshTokenRequest;
import com.henawedapp.backend.dto.request.RegistrationRequest;
import com.henawedapp.backend.dto.response.AccountInfoResponse;
import com.henawedapp.backend.dto.response.LoginResponse;
import com.henawedapp.backend.dto.response.RegistrationResponse;
import com.henawedapp.backend.security.AccountUserDetails;
import com.henawedapp.backend.security.AuthenticationContext;
import com.henawedapp.backend.service.AuthenticationService;
import com.henawedapp.backend.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý authentication và registration.
 *
 * Endpoints:
 * - POST /api/v1/auth/login      → Đăng nhập
 * - POST /api/v1/auth/logout     → Đăng xuất
 * - POST /api/v1/auth/refresh    → Refresh token
 * - GET  /api/v1/auth/me         → Lấy thông tin tài khoản hiện tại
 * - POST /api/v1/auth/register   → Đăng ký tài khoản mới
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final AuthenticationContext authenticationContext;

    // ========== Authentication Endpoints ==========

    /**
     * Đăng nhập bằng email hoặc số điện thoại và mật khẩu.
     *
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for: {}", request.getContactValue());
        LoginResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Đăng xuất.
     * Token sẽ được blacklist và không thể sử dụng lại.
     *
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);
        String refreshToken = request.getParameter(REFRESH_TOKEN_COOKIE);

        authenticationService.logout(accessToken, refreshToken);

        return ResponseEntity.ok().build();
    }

    /**
     * Refresh access token bằng refresh token.
     *
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh attempt");
        LoginResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy thông tin tài khoản hiện tại.
     *
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<AccountInfoResponse> getCurrentAccount() {
        AccountUserDetails userDetails = authenticationContext.getCurrentUserDetails();
        AccountInfoResponse response = authenticationService.getCurrentAccountInfo(userDetails);
        return ResponseEntity.ok(response);
    }

    // ========== Registration Endpoints ==========

    /**
     * Đăng ký tài khoản hội viên mới.
     *
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody RegistrationRequest request) {

        log.info("Received registration request for email: {}", request.getEmail());

        RegistrationResponse response = registrationService.register(request);

        log.info("Registration successful for email: {}", request.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ========== Private Methods ==========

    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
