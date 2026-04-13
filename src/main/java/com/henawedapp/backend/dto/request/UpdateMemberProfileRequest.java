package com.henawedapp.backend.dto.request;

import com.henawedapp.backend.model.enums.VisibilityLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để cập nhật thông tin chung của MemberProfile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberProfileRequest {

    @Size(min = 1, max = 100, message = "Display name phải từ 1-100 ký tự")
    private String displayName;

    @Email(message = "Email không hợp lệ")
    private String contactEmail;

    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    private String contactPhone;

    @Size(max = 1000, message = "Bio không được quá 1000 ký tự")
    private String bio;

    private VisibilityLevel publicVisibility;
}
