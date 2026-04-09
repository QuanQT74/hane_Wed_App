package com.henawedapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu xác thực OTP để hoàn tất đăng ký.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpRequest {

    /**
     * Email hoặc phone đã dùng để đăng ký.
     */
    @NotBlank(message = "Email hoặc số điện thoại không được để trống")
    private String contactValue;

    /**
     * Mã OTP 6 số.
     */
    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP phải là 6 chữ số")
    private String otp;
}
