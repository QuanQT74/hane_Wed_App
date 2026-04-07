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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Xử lý đăng ký tài khoản hội viên mới.
     */
    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        // ========== 1. Validate duplicate email/phone ==========
        validateDuplicateEmail(request.getEmail());
        validateDuplicatePhone(request.getPhone());

        // ========== 2. Tạo Account ==========
        Account account = Account.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(hashPassword(request.getPassword())) // Cần implement password hashing
                .status(com.henawedapp.backend.model.enums.AccountStatus.PENDING)
                .isActivated(false)
                .createdAt(Instant.now())
                .build();

        account = accountRepository.save(account);
        log.info("Account created with ID: {}", account.getId());

        // ========== 3. Tạo MembershipApplication ==========
        MembershipApplication application = MembershipApplication.builder()
                .account(account)
                .requestedMemberType(request.getMemberType())
                .applicantName(request.getDisplayName())
                .applicantEmail(request.getEmail())
                .applicantPhone(request.getPhone())
                .status(ApplicationStatus.PENDING)
                .submittedAt(Instant.now())
                .build();

        application = membershipApplicationRepository.save(application);
        log.info("MembershipApplication created with ID: {}", application.getId());

        // ========== 4. Tạo MemberProfile ==========
        String memberCode = generateMemberCode();
        MemberProfile memberProfile = MemberProfile.builder()
                .account(account)
                .memberCode(memberCode)
                .profileType(request.getMemberType())
                .displayName(request.getDisplayName())
                .contactEmail(request.getContactEmail() != null ? request.getContactEmail() : request.getEmail())
                .contactPhone(request.getContactPhone() != null ? request.getContactPhone() : request.getPhone())
                .bio(request.getBio())
                .isActive(false) // Chưa active cho đến khi được duyệt
                .build();

        memberProfile = memberProfileRepository.save(memberProfile);
        log.info("MemberProfile created with ID: {}, memberCode: {}", memberProfile.getId(), memberCode);

        // ========== 5. Tạo IndividualProfile hoặc OrganizationProfile ==========
        if (request.getMemberType() == MemberType.INDIVIDUAL) {
            createIndividualProfile(request, memberProfile);
        } else {
            createOrganizationProfile(request, memberProfile);
        }

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
    }

    /**
     * Tạo IndividualProfile.
     */
    private void createIndividualProfile(RegistrationRequest request, MemberProfile memberProfile) {
        IndividualProfile individualProfile = IndividualProfile.builder()
                .memberProfile(memberProfile)
                .fullName(request.getFullName() != null ? request.getFullName() : request.getDisplayName())
                .occupation(request.getOccupation())
                .addressText(request.getAddressText())
                .build();

        // Set industry group nếu có
        if (request.getIndustryGroupCode() != null) {
            IndustryGroup industryGroup = industryGroupRepository
                    .findByCode(request.getIndustryGroupCode())
                    .orElseThrow(() -> new ResourceNotFoundException("IndustryGroup", "code", request.getIndustryGroupCode()));
            individualProfile.setIndustryGroup(industryGroup);
        }

        individualProfileRepository.save(individualProfile);
        log.info("IndividualProfile created for member: {}", memberProfile.getId());
    }

    /**
     * Tạo OrganizationProfile.
     */
    private void createOrganizationProfile(RegistrationRequest request, MemberProfile memberProfile) {
        OrganizationProfile organizationProfile = OrganizationProfile.builder()
                .memberProfile(memberProfile)
                .organizationName(request.getOrganizationName() != null ? request.getOrganizationName() : request.getDisplayName())
                .legalRepresentative(request.getLegalRepresentative())
                .website(request.getWebsite())
                .productServiceSummary(request.getProductServiceSummary())
                .addressText(request.getOrgAddressText())
                .build();

        // Set industry group nếu có
        if (request.getIndustryGroupCode() != null) {
            IndustryGroup industryGroup = industryGroupRepository
                    .findByCode(request.getIndustryGroupCode())
                    .orElseThrow(() -> new ResourceNotFoundException("IndustryGroup", "code", request.getIndustryGroupCode()));
            organizationProfile.setIndustryGroup(industryGroup);
        }

        organizationProfileRepository.save(organizationProfile);
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
