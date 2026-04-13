package com.henawedapp.backend.controller;

import com.henawedapp.backend.dto.request.UpdateAttachmentVisibilityRequest;
import com.henawedapp.backend.dto.request.UpdateIndividualProfileRequest;
import com.henawedapp.backend.dto.request.UpdateMemberProfileRequest;
import com.henawedapp.backend.dto.request.UpdateOrganizationProfileRequest;
import com.henawedapp.backend.dto.response.MemberProfileResponse;
import com.henawedapp.backend.model.enums.AttachmentType;
import com.henawedapp.backend.service.MemberProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller xử lý Member Profile Management (FR-04).
 *
 * API Endpoints:
 * - GET  /api/v1/member-profile/me                        → Xem hồ sơ của mình
 * - PUT  /api/v1/member-profile/me                        → Cập nhật hồ sơ chung
 * - PUT  /api/v1/member-profile/me/individual             → Cập nhật IndividualProfile
 * - PUT  /api/v1/member-profile/me/organization            → Cập nhật OrganizationProfile
 * - POST /api/v1/member-profile/me/avatar                  → Upload avatar
 * - POST /api/v1/member-profile/me/attachments            → Upload attachment
 * - PUT  /api/v1/member-profile/me/attachments/{id}/visibility → Cập nhật visibility
 * - DELETE /api/v1/member-profile/me/attachments/{id}     → Xóa attachment
 */
@RestController
@RequestMapping("/api/v1/member-profile")
@RequiredArgsConstructor
@Slf4j
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    /**
     * Xem hồ sơ của chính mình.
     */
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile() {
        log.info("GET /api/v1/member-profile/me");
        MemberProfileResponse response = memberProfileService.getMyProfile();
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật thông tin chung của MemberProfile.
     */
    @PutMapping("/me")
    public ResponseEntity<MemberProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateMemberProfileRequest request) {
        log.info("PUT /api/v1/member-profile/me");
        MemberProfileResponse response = memberProfileService.updateMyProfile(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật IndividualProfile (chỉ cho INDIVIDUAL member).
     */
    @PutMapping("/me/individual")
    public ResponseEntity<MemberProfileResponse> updateMyIndividualProfile(
            @Valid @RequestBody UpdateIndividualProfileRequest request) {
        log.info("PUT /api/v1/member-profile/me/individual");
        MemberProfileResponse response = memberProfileService.updateMyIndividualProfile(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật OrganizationProfile (chỉ cho ORGANIZATION member).
     */
    @PutMapping("/me/organization")
    public ResponseEntity<MemberProfileResponse> updateMyOrganizationProfile(
            @Valid @RequestBody UpdateOrganizationProfileRequest request) {
        log.info("PUT /api/v1/member-profile/me/organization");
        MemberProfileResponse response = memberProfileService.updateMyOrganizationProfile(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload avatar cho profile của mình.
     */
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberProfileResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/member-profile/me/avatar");
        MemberProfileResponse response = memberProfileService.uploadAvatar(file);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload attachment cho profile của mình.
     */
    @PostMapping(value = "/me/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberProfileResponse> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("attachmentType") AttachmentType attachmentType,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic) {
        log.info("POST /api/v1/member-profile/me/attachments");
        MemberProfileResponse response = memberProfileService.uploadAttachment(file, attachmentType, isPublic);
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật visibility của attachment.
     */
    @PutMapping("/me/attachments/{attachmentId}/visibility")
    public ResponseEntity<MemberProfileResponse> updateAttachmentVisibility(
            @PathVariable UUID attachmentId,
            @Valid @RequestBody UpdateAttachmentVisibilityRequest request) {
        log.info("PUT /api/v1/member-profile/me/attachments/{}/visibility", attachmentId);
        MemberProfileResponse response = memberProfileService.updateAttachmentVisibility(attachmentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa attachment.
     */
    @DeleteMapping("/me/attachments/{attachmentId}")
    public ResponseEntity<MemberProfileResponse> deleteAttachment(
            @PathVariable UUID attachmentId) {
        log.info("DELETE /api/v1/member-profile/me/attachments/{}", attachmentId);
        MemberProfileResponse response = memberProfileService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(response);
    }
}