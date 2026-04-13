package com.henawedapp.backend.service;

import com.henawedapp.backend.dto.request.UpdateAttachmentVisibilityRequest;
import com.henawedapp.backend.dto.request.UpdateIndividualProfileRequest;
import com.henawedapp.backend.dto.request.UpdateMemberProfileRequest;
import com.henawedapp.backend.dto.request.UpdateOrganizationProfileRequest;
import com.henawedapp.backend.dto.response.MemberProfileResponse;
import com.henawedapp.backend.dto.response.MemberProfileResponse.AttachmentResponse;
import com.henawedapp.backend.dto.response.MemberProfileResponse.IndividualProfileDetail;
import com.henawedapp.backend.dto.response.MemberProfileResponse.OrganizationProfileDetail;
import com.henawedapp.backend.exception.ForbiddenAccessException;
import com.henawedapp.backend.exception.InvalidMemberTypeException;
import com.henawedapp.backend.exception.ResourceNotFoundException;
import com.henawedapp.backend.model.entity.Account;
import com.henawedapp.backend.model.entity.IndustryGroup;
import com.henawedapp.backend.model.entity.IndividualProfile;
import com.henawedapp.backend.model.entity.MemberProfile;
import com.henawedapp.backend.model.entity.OrganizationProfile;
import com.henawedapp.backend.model.entity.ProfileAttachment;
import com.henawedapp.backend.model.enums.AttachmentType;
import com.henawedapp.backend.model.enums.MemberType;
import com.henawedapp.backend.repository.AccountRepository;
import com.henawedapp.backend.repository.IndividualProfileRepository;
import com.henawedapp.backend.repository.IndustryGroupRepository;
import com.henawedapp.backend.repository.MemberProfileRepository;
import com.henawedapp.backend.repository.OrganizationProfileRepository;
import com.henawedapp.backend.repository.ProfileAttachmentRepository;
import com.henawedapp.backend.security.AuthenticationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service xử lý Member Profile Management (FR-04).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final IndividualProfileRepository individualProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final ProfileAttachmentRepository profileAttachmentRepository;
    private final IndustryGroupRepository industryGroupRepository;
    private final AccountRepository accountRepository;
    private final AuthenticationContext authenticationContext;
    private final FileStorageService fileStorageService;

    @PersistenceContext
    private EntityManager entityManager;

    // ========== GET Profile ==========

    /**
     * Lấy profile của current user.
     */
    @Transactional(readOnly = true)
    public MemberProfileResponse getMyProfile() {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        return buildMemberProfileResponse(memberProfile);
    }

    /**
     * Lấy profile của một member theo member code (public view).
     */
    @Transactional(readOnly = true)
    public MemberProfileResponse getPublicProfile(String memberCode) {
        MemberProfile memberProfile = memberProfileRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "memberCode", memberCode));

        // Chỉ trả về public fields hoặc fields có visibility phù hợp
        return buildMemberProfileResponse(memberProfile);
    }

    // ========== UPDATE MemberProfile (common fields) ==========

    /**
     * Cập nhật thông tin chung của MemberProfile.
     */
    @Transactional
    public MemberProfileResponse updateMyProfile(UpdateMemberProfileRequest request) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        // Update fields
        if (request.getDisplayName() != null) {
            memberProfile.setDisplayName(request.getDisplayName());
        }
        if (request.getContactEmail() != null) {
            memberProfile.setContactEmail(request.getContactEmail());
        }
        if (request.getContactPhone() != null) {
            memberProfile.setContactPhone(request.getContactPhone());
        }
        if (request.getBio() != null) {
            memberProfile.setBio(request.getBio());
        }
        if (request.getPublicVisibility() != null) {
            memberProfile.setPublicVisibility(request.getPublicVisibility());
        }

        memberProfile.setUpdatedAt(Instant.now());
        entityManager.merge(memberProfile);

        log.info("Updated member profile for accountId: {}", accountId);

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== UPDATE IndividualProfile ==========

    /**
     * Cập nhật IndividualProfile (chỉ cho INDIVIDUAL member).
     */
    @Transactional
    public MemberProfileResponse updateMyIndividualProfile(UpdateIndividualProfileRequest request) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        if (memberProfile.getProfileType() != MemberType.INDIVIDUAL) {
            throw new InvalidMemberTypeException("Chỉ hội viên cá nhân mới có thể cập nhật IndividualProfile.");
        }

        IndividualProfile individualProfile = individualProfileRepository
                .findByMemberProfileId(memberProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("IndividualProfile", "memberProfileId", memberProfile.getId().toString()));

        // Update fields
        if (request.getFullName() != null) {
            individualProfile.setFullName(request.getFullName());
            // Also update MemberProfile displayName if not set
            if (memberProfile.getDisplayName() == null || memberProfile.getDisplayName().isBlank()) {
                memberProfile.setDisplayName(request.getFullName());
            }
        }
        if (request.getOccupation() != null) {
            individualProfile.setOccupation(request.getOccupation());
        }
        if (request.getIndustryGroupCode() != null) {
            IndustryGroup industryGroup = industryGroupRepository.findById(request.getIndustryGroupCode())
                    .orElse(null);
            individualProfile.setIndustryGroup(industryGroup);
        }
        if (request.getAddressText() != null) {
            individualProfile.setAddressText(request.getAddressText());
        }

        entityManager.merge(individualProfile);
        entityManager.merge(memberProfile);

        log.info("Updated individual profile for accountId: {}", accountId);

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== UPDATE OrganizationProfile ==========

    /**
     * Cập nhật OrganizationProfile (chỉ cho ORGANIZATION member).
     */
    @Transactional
    public MemberProfileResponse updateMyOrganizationProfile(UpdateOrganizationProfileRequest request) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        if (memberProfile.getProfileType() != MemberType.ORGANIZATION) {
            throw new InvalidMemberTypeException("Chỉ hội viên tổ chức mới có thể cập nhật OrganizationProfile.");
        }

        OrganizationProfile organizationProfile = organizationProfileRepository
                .findByMemberProfileId(memberProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("OrganizationProfile", "memberProfileId", memberProfile.getId().toString()));

        // Update fields
        if (request.getOrganizationName() != null) {
            organizationProfile.setOrganizationName(request.getOrganizationName());
            // Also update MemberProfile displayName if not set
            if (memberProfile.getDisplayName() == null || memberProfile.getDisplayName().isBlank()) {
                memberProfile.setDisplayName(request.getOrganizationName());
            }
        }
        if (request.getLegalRepresentative() != null) {
            organizationProfile.setLegalRepresentative(request.getLegalRepresentative());
        }
        if (request.getIndustryGroupCode() != null) {
            IndustryGroup industryGroup = industryGroupRepository.findById(request.getIndustryGroupCode())
                    .orElse(null);
            organizationProfile.setIndustryGroup(industryGroup);
        }
        if (request.getWebsite() != null) {
            organizationProfile.setWebsite(request.getWebsite());
        }
        if (request.getProductServiceSummary() != null) {
            organizationProfile.setProductServiceSummary(request.getProductServiceSummary());
        }
        if (request.getAddressText() != null) {
            organizationProfile.setAddressText(request.getAddressText());
        }

        entityManager.merge(organizationProfile);
        entityManager.merge(memberProfile);

        log.info("Updated organization profile for accountId: {}", accountId);

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== AVATAR UPLOAD ==========

    /**
     * Upload avatar cho current user.
     */
    @Transactional
    public MemberProfileResponse uploadAvatar(MultipartFile file) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        // Validate file type (chỉ cho phép image)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new com.henawedapp.backend.exception.InvalidFileException("Chỉ cho phép upload file hình ảnh.");
        }

        // Store file
        String fileUrl = fileStorageService.storeFile(file, "avatars");

        // Delete old avatar if exists
        if (memberProfile.getAvatarUrl() != null) {
            fileStorageService.deleteFile(memberProfile.getAvatarUrl());
        }

        // Update avatar URL
        memberProfile.setAvatarUrl(fileUrl);
        memberProfile.setUpdatedAt(Instant.now());
        entityManager.merge(memberProfile);

        log.info("Updated avatar for accountId: {}, fileUrl: {}", accountId, fileUrl);

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== ATTACHMENT UPLOAD ==========

    /**
     * Upload attachment cho current user.
     */
    @Transactional
    public MemberProfileResponse uploadAttachment(MultipartFile file, AttachmentType attachmentType, Boolean isPublic) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        // Store file
        String fileUrl = fileStorageService.storeFile(file, "attachments");

        // Create attachment
        ProfileAttachment attachment = ProfileAttachment.builder()
                .memberProfile(memberProfile)
                .attachmentType(attachmentType)
                .fileUrl(fileUrl)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .isPublic(isPublic != null ? isPublic : false)
                .uploadedAt(Instant.now())
                .build();

        entityManager.persist(attachment);

        log.info("Uploaded attachment for memberProfileId: {}, fileName: {}", memberProfile.getId(), file.getOriginalFilename());

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== UPDATE ATTACHMENT VISIBILITY ==========

    /**
     * Cập nhật visibility của attachment.
     */
    @Transactional
    public MemberProfileResponse updateAttachmentVisibility(UUID attachmentId, UpdateAttachmentVisibilityRequest request) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        // Verify attachment belongs to this member profile
        ProfileAttachment attachment = profileAttachmentRepository
                .findByIdAndMemberProfileId(attachmentId, memberProfile.getId())
                .orElseThrow(() -> new ForbiddenAccessException("Attachment không thuộc về bạn hoặc không tồn tại."));

        attachment.setIsPublic(request.getIsPublic());
        entityManager.merge(attachment);

        log.info("Updated attachment visibility: {}, isPublic: {}", attachmentId, request.getIsPublic());

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== DELETE ATTACHMENT ==========

    /**
     * Xóa attachment.
     */
    @Transactional
    public MemberProfileResponse deleteAttachment(UUID attachmentId) {
        UUID accountId = authenticationContext.getCurrentAccountId();
        MemberProfile memberProfile = findMemberProfileByAccountId(accountId);

        // Verify attachment belongs to this member profile
        ProfileAttachment attachment = profileAttachmentRepository
                .findByIdAndMemberProfileId(attachmentId, memberProfile.getId())
                .orElseThrow(() -> new ForbiddenAccessException("Attachment không thuộc về bạn hoặc không tồn tại."));

        // Delete file
        fileStorageService.deleteFile(attachment.getFileUrl());

        // Delete attachment record
        profileAttachmentRepository.delete(attachment);

        log.info("Deleted attachment: {} for memberProfileId: {}", attachmentId, memberProfile.getId());

        return buildMemberProfileResponse(memberProfile);
    }

    // ========== PRIVATE HELPERS ==========

    /**
     * Tìm MemberProfile theo account ID.
     */
    private MemberProfile findMemberProfileByAccountId(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ForbiddenAccessException("Tài khoản không tồn tại."));

        if (account.getMemberProfile() == null) {
            throw new ResourceNotFoundException("MemberProfile", "accountId", accountId.toString());
        }

        return account.getMemberProfile();
    }

    /**
     * Build response DTO từ MemberProfile entity.
     */
    private MemberProfileResponse buildMemberProfileResponse(MemberProfile memberProfile) {
        List<ProfileAttachment> attachments = profileAttachmentRepository
                .findByMemberProfileId(memberProfile.getId());

        MemberProfileResponse response = MemberProfileResponse.builder()
                .id(memberProfile.getId())
                .memberCode(memberProfile.getMemberCode())
                .profileType(memberProfile.getProfileType())
                .displayName(memberProfile.getDisplayName())
                .avatarUrl(memberProfile.getAvatarUrl())
                .contactEmail(memberProfile.getContactEmail())
                .contactPhone(memberProfile.getContactPhone())
                .bio(memberProfile.getBio())
                .publicVisibility(memberProfile.getPublicVisibility())
                .isActive(memberProfile.getIsActive())
                .createdAt(memberProfile.getCreatedAt())
                .updatedAt(memberProfile.getUpdatedAt())
                .attachments(attachments.stream()
                        .map(this::buildAttachmentResponse)
                        .collect(Collectors.toList()))
                .build();

        // Load individual or organization profile based on type
        if (memberProfile.getProfileType() == MemberType.INDIVIDUAL) {
            individualProfileRepository.findByMemberProfileId(memberProfile.getId())
                    .ifPresent(indProfile -> {
                        response.setIndividualProfile(buildIndividualProfileDetail(indProfile));
                    });
        } else if (memberProfile.getProfileType() == MemberType.ORGANIZATION) {
            organizationProfileRepository.findByMemberProfileId(memberProfile.getId())
                    .ifPresent(orgProfile -> {
                        response.setOrganizationProfile(buildOrganizationProfileDetail(orgProfile));
                    });
        }

        return response;
    }

    private IndividualProfileDetail buildIndividualProfileDetail(IndividualProfile profile) {
        return IndividualProfileDetail.builder()
                .memberProfileId(profile.getMemberProfileId())
                .fullName(profile.getFullName())
                .occupation(profile.getOccupation())
                .industryGroupName(profile.getIndustryGroup() != null ? profile.getIndustryGroup().getName() : null)
                .industryGroupCode(profile.getIndustryGroup() != null ? profile.getIndustryGroup().getCode() : null)
                .addressText(profile.getAddressText())
                .build();
    }

    private OrganizationProfileDetail buildOrganizationProfileDetail(OrganizationProfile profile) {
        return OrganizationProfileDetail.builder()
                .memberProfileId(profile.getMemberProfileId())
                .organizationName(profile.getOrganizationName())
                .legalRepresentative(profile.getLegalRepresentative())
                .industryGroupName(profile.getIndustryGroup() != null ? profile.getIndustryGroup().getName() : null)
                .industryGroupCode(profile.getIndustryGroup() != null ? profile.getIndustryGroup().getCode() : null)
                .website(profile.getWebsite())
                .productServiceSummary(profile.getProductServiceSummary())
                .addressText(profile.getAddressText())
                .build();
    }

    private AttachmentResponse buildAttachmentResponse(ProfileAttachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .attachmentType(attachment.getAttachmentType())
                .fileUrl(attachment.getFileUrl())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .isPublic(attachment.getIsPublic())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}