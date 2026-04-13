package com.henawedapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO để cập nhật OrganizationProfile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrganizationProfileRequest {

    @NotBlank(message = "Tên tổ chức không được để trống")
    @Size(max = 200, message = "Tên tổ chức không được quá 200 ký tự")
    private String organizationName;

    @Size(max = 100, message = "Người đại diện không được quá 100 ký tự")
    private String legalRepresentative;

    private UUID industryGroupCode;

    @Size(max = 200, message = "Website không được quá 200 ký tự")
    private String website;

    @Size(max = 1000, message = "Mô tả sản phẩm/dịch vụ không được quá 1000 ký tự")
    private String productServiceSummary;

    @Size(max = 500, message = "Địa chỉ không được quá 500 ký tự")
    private String addressText;
}