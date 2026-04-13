package com.henawedapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO để cập nhật IndividualProfile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateIndividualProfileRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;

    @Size(max = 100, message = "Nghề nghiệp không được quá 100 ký tự")
    private String occupation;

    private UUID industryGroupCode;

    @Size(max = 500, message = "Địa chỉ không được quá 500 ký tự")
    private String addressText;
}