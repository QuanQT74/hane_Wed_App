package com.henawedapp.backend.service;

import com.henawedapp.backend.dto.request.RegistrationRequest;
import com.henawedapp.backend.dto.response.RegistrationResponse;
import com.henawedapp.backend.exception.DuplicateResourceException;
import com.henawedapp.backend.exception.ResourceNotFoundException;
import com.henawedapp.backend.model.entity.*;
import com.henawedapp.backend.model.enums.ApplicationStatus;
import com.henawedapp.backend.model.enums.MemberType;
import com.henawedapp.backend.model.enums.NotificationChannel;
import com.henawedapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.Instant;
import java.util.UUID;

/**
 * Service xử lý đăng ký tài khoản hội viên.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final AccountRepository accountRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MembershipApplicationRepository membershipApplicationRepository;
    private final IndividualProfileRepository individualProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final IndustryGroupRepository industryGroupRepository;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Xử lý đăng ký tài khoản hội viên mới.
     */
    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        try {
            // ========== 1. Validate duplicate email/phone ==========
            validateDuplicateEmail(request.getEmail());
            validateDuplicatePhone(request.getPhone());

            // ========== 2. Tạo Account ==========
            log.info("Creating account for email: {}", request.getEmail());
            Instant now = Instant.now();
            Account account = Account.builder()
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(hashPassword(request.getPassword()))
                    .status(com.henawedapp.backend.model.enums.AccountStatus.PENDING)
                    .isActivated(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .lastLoginAt(now)
                    .build();

            entityManager.persist(account);
            entityManager.flush();
            log.info("Account persisted with ID: {}", account.getId());

            // ========== 3. Tạo MembershipApplication ==========
            log.info("Creating membership application for account: {}", account.getId());
            MembershipApplication application = MembershipApplication.builder()
                    .account(account)
                    .requestedMemberType(request.getMemberType())
                    .applicantName(request.getDisplayName())
                    .applicantEmail(request.getEmail())
                    .applicantPhone(request.getPhone())
                    .status(ApplicationStatus.PENDING)
                    .submittedAt(now)
                    .createdAt(now)
                    .build();

            entityManager.persist(application);
            entityManager.flush();
            log.info("MembershipApplication persisted with ID: {}", application.getId());

            // ========== 4. Tạo MemberProfile ==========
            log.info("Creating member profile for account: {}", account.getId());
            String memberCode = generateMemberCode();
            MemberProfile memberProfile = MemberProfile.builder()
                    .account(account)
                    .memberCode(memberCode)
                    .profileType(request.getMemberType())
                    .displayName(request.getDisplayName())
                    .contactEmail(request.getEmail())
                    .contactPhone(request.getPhone())
                    .bio(request.getBio())
                    .isActive(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // Use EntityManager directly for better control
            entityManager.persist(memberProfile);
            entityManager.flush();
            log.info("MemberProfile persisted directly - ID: {}", memberProfile.getId());

            if (memberProfile.getId() == null) {
                throw new RuntimeException("MemberProfile ID is null after persist!");
            }

            // ========== 5. Tạo IndividualProfile hoặc OrganizationProfile ==========
            log.info("Creating {} profile for member: {}", request.getMemberType(), memberProfile.getId());
            createMemberTypeProfile(request, memberProfile);

            // ========== 6. Gửi thông báo xác nhận ==========
            sendConfirmationNotification(account, request.getDisplayName());

            log.info("Registration completed successfully for email: {}", request.getEmail());

            return RegistrationResponse.builder()
                    .accountId(account.getId())
                    .applicationId(application.getId())
                    .email(account.getEmail())
                    .memberType(request.getMemberType().name())
                    .status(ApplicationStatus.PENDING.name())
                    .message("Đăng ký thành công! Vui lòng chờ quản trị viên phê duyệt.")
                    .submittedAt(application.getSubmittedAt())
                    .build();

        } catch (Exception e) {
            log.error("Registration failed for email: {}, error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tạo IndividualProfile hoặc OrganizationProfile.
     */
    private void createMemberTypeProfile(RegistrationRequest request, MemberProfile memberProfile) {
        if (request.getMemberType() == MemberType.INDIVIDUAL) {
            createIndividualProfile(request, memberProfile);
        } else {
            createOrganizationProfile(request, memberProfile);
        }
    }

    /**
     * Tạo IndividualProfile.
     */
    private void createIndividualProfile(RegistrationRequest request, MemberProfile memberProfile) {
        log.info("createIndividualProfile - memberProfile.id = {}", memberProfile.getId());

        RegistrationRequest.IndividualProfileRequest individualReq = request.getIndividualProfile();
        if (individualReq == null) {
            throw new IllegalArgumentException("Individual profile data is required for INDIVIDUAL member type");
        }

        IndividualProfile individualProfile = IndividualProfile.builder()
                .memberProfile(memberProfile)
                .memberProfileId(memberProfile.getId())
                .fullName(individualReq.getFullName() != null ? individualReq.getFullName() : request.getDisplayName())
                .occupation(individualReq.getOccupation())
                .addressText(individualReq.getAddressText())
                .build();

        // Set industry group nếu có
        if (request.getIndustryGroupCode() != null) {
            IndustryGroup industryGroup = industryGroupRepository
                    .findByCode(request.getIndustryGroupCode())
                    .orElseThrow(() -> new ResourceNotFoundException("IndustryGroup", "code", request.getIndustryGroupCode()));
            individualProfile.setIndustryGroup(industryGroup);
        }

        entityManager.persist(individualProfile);
        entityManager.flush();
        log.info("IndividualProfile created for member: {}", memberProfile.getId());
    }

    /**
     * Tạo OrganizationProfile.
     */
    private void createOrganizationProfile(RegistrationRequest request, MemberProfile memberProfile) {
        log.info("createOrganizationProfile - memberProfile.id = {}", memberProfile.getId());

        RegistrationRequest.OrganizationProfileRequest orgReq = request.getOrganizationProfile();
        if (orgReq == null) {
            throw new IllegalArgumentException("Organization profile data is required for ORGANIZATION member type");
        }

        OrganizationProfile organizationProfile = OrganizationProfile.builder()
                .memberProfile(memberProfile)
                .memberProfileId(memberProfile.getId())
                .organizationName(orgReq.getOrganizationName() != null ? orgReq.getOrganizationName() : request.getDisplayName())
                .legalRepresentative(orgReq.getLegalRepresentative())
                .website(orgReq.getWebsite())
                .productServiceSummary(orgReq.getProductServiceSummary())
                .addressText(orgReq.getAddressText())
                .build();

        // Set industry group nếu có
        if (request.getIndustryGroupCode() != null) {
            IndustryGroup industryGroup = industryGroupRepository
                    .findByCode(request.getIndustryGroupCode())
                    .orElseThrow(() -> new ResourceNotFoundException("IndustryGroup", "code", request.getIndustryGroupCode()));
            organizationProfile.setIndustryGroup(industryGroup);
        }

        entityManager.persist(organizationProfile);
        entityManager.flush();
        log.info("OrganizationProfile created for member: {}", memberProfile.getId());
    }

    /**
     * Gửi thông báo xác nhận đã tiếp nhận đăng ký.
     */
    private void sendConfirmationNotification(Account account, String displayName) {
        String title = "Xác nhận đăng ký thành viên";
        String content = String.format(
                "Xin chào %s,\n\n" +
                "Chúng tôi đã tiếp nhận đơn đăng ký thành viên của bạn.\n" +
                "Đơn đăng ký đang trong trạng thái chờ phê duyệt.\n" +
                "Chúng tôi sẽ thông báo cho bạn khi đơn được xử lý.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống Hane Web App",
                displayName
        );

        // Gửi notification trong hệ thống
        notificationService.sendNotification(
                account,
                title,
                content,
                NotificationChannel.IN_APP
        );

        // Gửi email notification (mock)
        notificationService.sendEmailNotification(account, title, content);
    }

    /**
     * Validate email không trùng.
     */
    private void validateDuplicateEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email", "email", email);
        }
    }

    /**
     * Validate phone không trùng.
     */
    private void validateDuplicatePhone(String phone) {
        if (accountRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException("Số điện thoại", "phone", phone);
        }
    }

    /**
     * Generate unique member code.
     * Format: MEM-XXXXXXXX (8 random chars)
     */
    private String generateMemberCode() {
        String code;
        do {
            code = "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (memberProfileRepository.existsByMemberCode(code));
        return code;
    }

    /**
     * Hash password using BCrypt.
     */
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
