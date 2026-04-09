package com.henawedapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu gửi lại OTP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResendOtpRequest {

    /**
     * Email hoặc phone đã dùng để đăng ký.
     */
    @NotBlank(message = "Email hoặc số điện thoại không được để trống")
    private String contactValue;
}
