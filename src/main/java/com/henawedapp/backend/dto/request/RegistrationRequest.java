package com.henawedapp.backend.dto.request;

import com.henawedapp.backend.model.enums.MemberType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO yêu cầu đăng ký tài khoản hội viên.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {

    // ========== Account Info ==========

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại không đúng định dạng (0xxxxxxxxx)")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    // ========== Profile Info ==========

    @NotNull(message = "Loại hội viên không được để trống")
    private MemberType memberType;

    @NotBlank(message = "Tên hiển thị không được để trống")
    @Size(max = 255, message = "Tên hiển thị không được vượt quá 255 ký tự")
    private String displayName;

    private String bio;

    // ========== Individual Profile (nếu memberType = INDIVIDUAL) ==========

    @Valid
    private IndividualProfileRequest individualProfile;

    // ========== Organization Profile (nếu memberType = ORGANIZATION) ==========

    @Valid
    private OrganizationProfileRequest organizationProfile;

    // ========== Industry Group ==========

    private String industryGroupCode;

    // ========== Attachments ==========

    @Valid
    private List<AttachmentRequest> attachments;

    /**
     * DTO cho hồ sơ cá nhân.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IndividualProfileRequest {

        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 255, message = "Họ tên không được vượt quá 255 ký tự")
        private String fullName;

        private String occupation;

        private String addressText;
    }

    /**
     * DTO cho hồ sơ tổ chức.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrganizationProfileRequest {

        @NotBlank(message = "Tên tổ chức không được để trống")
        @Size(max = 255, message = "Tên tổ chức không được vượt quá 255 ký tự")
        private String organizationName;

        private String legalRepresentative;

        private String website;

        private String productServiceSummary;

        private String addressText;
    }

    /**
     * DTO cho tệp đính kèm.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentRequest {

        @NotBlank(message = "Loại tệp đính kèm không được để trống")
        private String attachmentType;

        @NotBlank(message = "URL tệp không được để trống")
        private String fileUrl;
    }
}
