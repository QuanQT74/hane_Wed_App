package com.henawedapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /**
     * Email hoặc số điện thoại của tài khoản.
     */
    @NotBlank(message = "Email hoặc số điện thoại không được để trống")
    private String contactValue;

    /**
     * Mật khẩu.
     */
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
