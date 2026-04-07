package com.henawedapp.backend.controller;

import com.henawedapp.backend.dto.request.RegistrationRequest;
import com.henawedapp.backend.dto.response.RegistrationResponse;
import com.henawedapp.backend.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý authentication và registration.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RegistrationService registrationService;

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
}
