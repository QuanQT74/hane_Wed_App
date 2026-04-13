package com.henawedapp.backend.dto.response;

import com.henawedapp.backend.model.enums.AttachmentType;
import com.henawedapp.backend.model.enums.MemberType;
import com.henawedapp.backend.model.enums.VisibilityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO cho MemberProfile chi tiết.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileResponse {

    // ===== MemberProfile fields =====
    private UUID id;
    private String memberCode;
    private MemberType profileType;
    private String displayName;
    private String avatarUrl;
    private String contactEmail;
    private String contactPhone;
    private String bio;
    private VisibilityLevel publicVisibility;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    // ===== Individual Profile (nếu là INDIVIDUAL) =====
    private IndividualProfileDetail individualProfile;

    // ===== Organization Profile (nếu là ORGANIZATION) =====
    private OrganizationProfileDetail organizationProfile;

    // ===== Attachments =====
    private List<AttachmentResponse> attachments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IndividualProfileDetail {
        private UUID memberProfileId;
        private String fullName;
        private String occupation;
        private String industryGroupName;
        private String industryGroupCode;
        private String addressText;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrganizationProfileDetail {
        private UUID memberProfileId;
        private String organizationName;
        private String legalRepresentative;
        private String industryGroupName;
        private String industryGroupCode;
        private String website;
        private String productServiceSummary;
        private String addressText;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentResponse {
        private UUID id;
        private AttachmentType attachmentType;
        private String fileUrl;
        private String fileName;
        private Long fileSize;
        private Boolean isPublic;
        private Instant uploadedAt;
    }
}