package com.henawedapp.backend.service;

import com.henawedapp.backend.dto.request.InitiateRegistrationRequest;
import com.henawedapp.backend.dto.request.ResendOtpRequest;
import com.henawedapp.backend.dto.request.VerifyOtpRequest;
import com.henawedapp.backend.dto.response.InitiateRegistrationResponse;
import com.henawedapp.backend.dto.response.ResendOtpResponse;
import com.henawedapp.backend.dto.response.VerifyOtpResponse;
import com.henawedapp.backend.exception.*;
import com.henawedapp.backend.model.entity.*;
import com.henawedapp.backend.model.enums.*;
import com.henawedapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Service xử lý quy trình đăng ký với xác thực OTP.
 *
 * Flow:
 * 1. initiateRegistration() - Tạo PendingRegistration, sinh OTP, gửi cho user
 * 2. verifyOtp() - Xác thực OTP, tạo Account + MemberProfile thực sự
 * 3. resendOtp() - Gửi lại OTP mới
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationVerificationService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int REGISTRATION_EXPIRY_MINUTES = 15;
    private static final int MAX_RESEND_COUNT = 3;
    private static final int MAX_OTP_ATTEMPTS = 5;

    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final OtpRepository otpRepository;
    private final IndividualProfileDataRepository individualProfileDataRepository;
    private final OrganizationProfileDataRepository organizationProfileDataRepository;
    private final AccountRepository accountRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final MembershipApplicationRepository membershipApplicationRepository;
    private final IndividualProfileRepository individualProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final IndustryGroupRepository industryGroupRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SmsService smsService;

    @PersistenceContext
    private EntityManager entityManager;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ========== STEP 1: Initiate Registration ==========

    /**
     * Bước 1: Khởi tạo đăng ký - Tạo PendingRegistration, sinh OTP, gửi cho user.
     */
    @Transactional
    public InitiateRegistrationResponse initiateRegistration(InitiateRegistrationRequest request) {
        log.info("Initiating registration for contact: {}",
                request.getEmail() != null ? request.getEmail() : request.getPhone());

        // Xác định contact type và value
        String contactValue = resolveContactValue(request.getEmail(), request.getPhone());
        String contactType = request.getEmail() != null ? "EMAIL" : "PHONE";

        // 1. Validate duplicate trong Account đã tồn tại
        validateNotExistingAccount(contactValue, contactType);

        // 2. Cancel any existing pending registration with same contact
        cancelExistingPendingRegistration(contactValue);

        // 3. Tạo PendingRegistration
        PendingRegistration pendingReg = createPendingRegistration(request, contactValue, contactType);

        // 4. Tạo OTP và gửi
        Otp otp = createAndSendOtp(pendingReg, contactValue, contactType);

        log.info("Registration initiated successfully. PendingRegId: {}", pendingReg.getId());

        return InitiateRegistrationResponse.builder()
                .pendingRegistrationId(pendingReg.getId().toString())
                .otpChannel(contactType)
                .otpExpiresAt(otp.getExpiresAt())
                .registrationExpiresAt(pendingReg.getExpiresAt())
                .remainingResendCount(MAX_RESEND_COUNT - pendingReg.getResendCount())
                .message(String.format("Mã OTP đã được gửi qua %s. Vui lòng nhập mã để xác thực.", contactType))
                .build();
    }

    // ========== STEP 2: Verify OTP ==========

    /**
     * Bước 2: Xác thực OTP - Nếu đúng, tạo Account thực sự.
     */
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        log.info("Verifying OTP for contact: {}", request.getContactValue());

        // 1. Tìm PendingRegistration
        PendingRegistration pendingReg = pendingRegistrationRepository
                .findByContactValueAndStatus(request.getContactValue(), PendingRegistrationStatus.PENDING)
                .orElseThrow(() -> new PendingRegistrationNotFoundException(
                        request.getContactValue().contains("@") ? "email" : "phone",
                        request.getContactValue()));

        // 2. Kiểm tra PendingRegistration đã hết hạn chưa
        if (pendingReg.isExpired()) {
            pendingReg.setStatus(PendingRegistrationStatus.EXPIRED);
            throw new RegistrationExpiredException("Đăng ký đã hết hạn. Vui lòng đăng ký lại.");
        }

        // 3. Tìm OTP
        Otp otp = otpRepository.findByPendingRegistrationIdAndStatus(
                        pendingReg.getId(), OtpStatus.ACTIVE)
                .orElseThrow(() -> new InvalidOtpException("Không tìm thấy OTP hợp lệ. Vui lòng yêu cầu gửi lại OTP."));

        // 4. Kiểm tra OTP đã hết hạn chưa
        if (otp.isExpired()) {
            otp.markAsExpired();
            pendingReg.setStatus(PendingRegistrationStatus.EXPIRED);
            throw new OtpExpiredException("Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại OTP.");
        }

        // 5. Kiểm tra số lần thử
        if (otp.isMaxAttemptsExceeded()) {
            otp.markAsMaxAttemptsExceeded();
            pendingReg.setStatus(PendingRegistrationStatus.EXPIRED);
            throw new MaxOtpAttemptsExceededException(
                    "Bạn đã nhập sai quá nhiều lần. Vui lòng yêu cầu gửi lại OTP.");
        }

        // 6. So sánh OTP (hash trước khi so sánh)
        String otpHash = hashOtp(request.getOtp());
        if (!MessageDigest.isEqual(otp.getOtpHash().getBytes(), otpHash.getBytes())) {
            otp.incrementAttempts();
            entityManager.merge(otp);
            int remainingAttempts = otp.getMaxAttempts() - otp.getAttempts();
            throw new InvalidOtpException(
                    String.format("Mã OTP không đúng. Bạn còn %d lần thử.", remainingAttempts));
        }

        // 7. OTP hợp lệ - Đánh dấu OTP đã sử dụng
        otp.markAsUsed();
        entityManager.merge(otp);

        // 8. Tạo Account, MembershipApplication, MemberProfile thực sự
        Account account = createActualAccount(pendingReg);

        log.info("OTP verified and account created successfully. AccountId: {}", account.getId());

        return VerifyOtpResponse.builder()
                .accountId(account.getId())
                .applicationId(account.getMemberProfile().getId()) // Using MemberProfile ID as proxy
                .memberCode(account.getMemberProfile().getMemberCode())
                .email(account.getEmail())
                .phone(account.getPhone())
                .memberType(pendingReg.getMemberType().name())
                .accountStatus(AccountStatus.PENDING_APPROVAL.name())
                .verifiedAt(Instant.now())
                .message("Đăng ký thành công! Tài khoản đang chờ quản trị viên phê duyệt.")
                .build();
    }

    // ========== STEP 3: Resend OTP ==========

    /**
     * Bước 3: Gửi lại OTP.
     */
    @Transactional
    public ResendOtpResponse resendOtp(ResendOtpRequest request) {
        log.info("Resending OTP for contact: {}", request.getContactValue());

        // 1. Tìm PendingRegistration
        PendingRegistration pendingReg = pendingRegistrationRepository
                .findByContactValueAndStatus(request.getContactValue(), PendingRegistrationStatus.PENDING)
                .orElseThrow(() -> new PendingRegistrationNotFoundException(
                        request.getContactValue().contains("@") ? "email" : "phone",
                        request.getContactValue()));

        // 2. Kiểm tra đã hết hạn chưa
        if (pendingReg.isExpired()) {
            pendingReg.setStatus(PendingRegistrationStatus.EXPIRED);
            throw new RegistrationExpiredException("Đăng ký đã hết hạn. Vui lòng đăng ký lại.");
        }

        // 3. Kiểm tra số lần resend
        if (pendingReg.getResendCount() >= MAX_RESEND_COUNT) {
            pendingReg.setStatus(PendingRegistrationStatus.CANCELLED);
            throw new MaxOtpAttemptsExceededException(
                    "Bạn đã yêu cầu gửi lại OTP quá nhiều lần. Vui lòng đăng ký lại.");
        }

        // 4. Invalidate old OTP if exists
        otpRepository.findByPendingRegistrationId(pendingReg.getId())
                .ifPresent(oldOtp -> {
                    oldOtp.markAsExpired();
                    entityManager.merge(oldOtp);
                });

        // 5. Tạo OTP mới
        Otp newOtp = createAndSendOtp(pendingReg, pendingReg.getContactValue(), pendingReg.getContactType());

        log.info("OTP resent successfully for pendingRegId: {}", pendingReg.getId());

        return ResendOtpResponse.builder()
                .otpChannel(pendingReg.getContactType())
                .otpExpiresAt(newOtp.getExpiresAt())
                .remainingResendCount(MAX_RESEND_COUNT - pendingReg.getResendCount())
                .message(String.format("Mã OTP mới đã được gửi qua %s.", pendingReg.getContactType()))
                .build();
    }

    // ========== Private Helper Methods ==========

    /**
     * Resolve contact value từ email hoặc phone.
     */
    private String resolveContactValue(String email, String phone) {
        if (email != null && !email.isBlank()) {
            return email.toLowerCase().trim();
        }
        if (phone != null && !phone.isBlank()) {
            return phone.trim();
        }
        throw new IllegalArgumentException("Phải cung cấp email hoặc số điện thoại");
    }

    /**
     * Validate rằng email/phone chưa tồn tại trong Account.
     */
    private void validateNotExistingAccount(String contactValue, String contactType) {
        if ("EMAIL".equals(contactType)) {
            if (accountRepository.existsByEmail(contactValue)) {
                throw new DuplicateResourceException("Email", "email", contactValue);
            }
        } else {
            if (accountRepository.existsByPhone(contactValue)) {
                throw new DuplicateResourceException("Số điện thoại", "phone", contactValue);
            }
        }
    }

    /**
     * Cancel any existing pending registration with same contact.
     */
    private void cancelExistingPendingRegistration(String contactValue) {
        pendingRegistrationRepository.findByContactValueAndStatus(contactValue, PendingRegistrationStatus.PENDING)
                .ifPresent(existing -> {
                    existing.setStatus(PendingRegistrationStatus.CANCELLED);
                    entityManager.merge(existing);
                    log.info("Cancelled existing pending registration for contact: {}", contactValue);
                });
    }

    /**
     * Tạo PendingRegistration từ request.
     */
    private PendingRegistration createPendingRegistration(InitiateRegistrationRequest request, String contactValue, String contactType) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(REGISTRATION_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        PendingRegistration pendingReg = PendingRegistration.builder()
                .contactValue(contactValue)
                .contactType(contactType)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .bio(request.getBio())
                .memberType(request.getMemberType())
                .status(PendingRegistrationStatus.PENDING)
                .resendCount(0)
                .expiresAt(expiresAt)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(pendingReg);

        // Lưu profile data tương ứng
        if (request.getMemberType() == MemberType.INDIVIDUAL && request.getIndividualProfile() != null) {
            IndividualProfileData profileData = IndividualProfileData.builder()
                    .pendingRegistration(pendingReg)
                    .fullName(request.getIndividualProfile().getFullName())
                    .occupation(request.getIndividualProfile().getOccupation())
                    .addressText(request.getIndividualProfile().getAddressText())
                    .industryGroupCode(request.getIndustryGroupCode())
                    .createdAt(now)
                    .build();
            entityManager.persist(profileData);
        } else if (request.getMemberType() == MemberType.ORGANIZATION && request.getOrganizationProfile() != null) {
            OrganizationProfileData profileData = OrganizationProfileData.builder()
                    .pendingRegistration(pendingReg)
                    .organizationName(request.getOrganizationProfile().getOrganizationName())
                    .legalRepresentative(request.getOrganizationProfile().getLegalRepresentative())
                    .website(request.getOrganizationProfile().getWebsite())
                    .productServiceSummary(request.getOrganizationProfile().getProductServiceSummary())
                    .addressText(request.getOrganizationProfile().getAddressText())
                    .industryGroupCode(request.getIndustryGroupCode())
                    .createdAt(now)
                    .build();
            entityManager.persist(profileData);
        }

        entityManager.flush();
        return pendingReg;
    }

    /**
     * Tạo OTP và gửi cho user.
     */
    private Otp createAndSendOtp(PendingRegistration pendingReg, String contactValue, String contactType) {
        // Sinh OTP 6 số ngẫu nhiên
        String rawOtp = generateRawOtp();
        String otpHash = hashOtp(rawOtp);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        Otp otp = Otp.builder()
                .pendingRegistration(pendingReg)
                .otpHash(otpHash)
                .maxAttempts(MAX_OTP_ATTEMPTS)
                .attempts(0)
                .status(OtpStatus.ACTIVE)
                .expiresAt(expiresAt)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(otp);
        pendingReg.setOtp(otp);
        entityManager.merge(pendingReg);
        entityManager.flush();

        // Gửi OTP theo kênh
        if ("EMAIL".equals(contactType)) {
            emailService.sendOtpEmail(contactValue, rawOtp, OTP_EXPIRY_MINUTES);
        } else {
            smsService.sendOtpSms(contactValue, rawOtp, OTP_EXPIRY_MINUTES);
        }

        return otp;
    }

    /**
     * Sinh OTP ngẫu nhiên 6 số.
     */
    private String generateRawOtp() {
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000; // 100000 - 999999
        return String.valueOf(otp);
    }

    /**
     * Hash OTP bằng SHA-256.
     */
    private String hashOtp(String rawOtp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawOtp.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash OTP", e);
        }
    }

    /**
     * Tạo Account, MembershipApplication, MemberProfile thực sự sau khi OTP xác thực thành công.
     */
    private Account createActualAccount(PendingRegistration pendingReg) {
        Instant now = Instant.now();

        // 1. Tạo Account
        Account account = Account.builder()
                .email("EMAIL".equals(pendingReg.getContactType()) ? pendingReg.getContactValue() : null)
                .phone("PHONE".equals(pendingReg.getContactType()) ? pendingReg.getContactValue() : null)
                .passwordHash(pendingReg.getPasswordHash())
                .status(AccountStatus.PENDING_APPROVAL)
                .isActivated(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(account);
        entityManager.flush();

        // 2. Tạo MembershipApplication
        MembershipApplication application = MembershipApplication.builder()
                .account(account)
                .requestedMemberType(pendingReg.getMemberType())
                .applicantName(pendingReg.getDisplayName())
                .applicantEmail(account.getEmail())
                .applicantPhone(account.getPhone())
                .status(ApplicationStatus.PENDING)
                .submittedAt(now)
                .createdAt(now)
                .build();

        entityManager.persist(application);

        // 3. Tạo MemberProfile
        String memberCode = generateMemberCode();
        MemberProfile memberProfile = MemberProfile.builder()
                .account(account)
                .memberCode(memberCode)
                .profileType(pendingReg.getMemberType())
                .displayName(pendingReg.getDisplayName())
                .contactEmail(account.getEmail())
                .contactPhone(account.getPhone())
                .bio(pendingReg.getBio())
                .isActive(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(memberProfile);
        entityManager.flush();

        // 4. Tạo IndividualProfile hoặc OrganizationProfile
        if (pendingReg.getMemberType() == MemberType.INDIVIDUAL) {
            IndividualProfileData profileData = individualProfileDataRepository
                    .findByPendingRegistrationId(pendingReg.getId())
                    .orElse(null);

            IndividualProfile individualProfile = IndividualProfile.builder()
                    .memberProfile(memberProfile)
                    .memberProfileId(memberProfile.getId())
                    .fullName(profileData != null ? profileData.getFullName() : pendingReg.getDisplayName())
                    .occupation(profileData != null ? profileData.getOccupation() : null)
                    .addressText(profileData != null ? profileData.getAddressText() : null)
                    .build();

            if (profileData != null && profileData.getIndustryGroupCode() != null) {
                industryGroupRepository.findByCode(profileData.getIndustryGroupCode())
                        .ifPresent(individualProfile::setIndustryGroup);
            }

            entityManager.persist(individualProfile);
        } else {
            OrganizationProfileData profileData = organizationProfileDataRepository
                    .findByPendingRegistrationId(pendingReg.getId())
                    .orElse(null);

            OrganizationProfile organizationProfile = OrganizationProfile.builder()
                    .memberProfile(memberProfile)
                    .memberProfileId(memberProfile.getId())
                    .organizationName(profileData != null ? profileData.getOrganizationName() : pendingReg.getDisplayName())
                    .legalRepresentative(profileData != null ? profileData.getLegalRepresentative() : null)
                    .website(profileData != null ? profileData.getWebsite() : null)
                    .productServiceSummary(profileData != null ? profileData.getProductServiceSummary() : null)
                    .addressText(profileData != null ? profileData.getAddressText() : null)
                    .build();

            if (profileData != null && profileData.getIndustryGroupCode() != null) {
                industryGroupRepository.findByCode(profileData.getIndustryGroupCode())
                        .ifPresent(organizationProfile::setIndustryGroup);
            }

            entityManager.persist(organizationProfile);
        }

        // 5. Cập nhật PendingRegistration thành VERIFIED
        pendingReg.setStatus(PendingRegistrationStatus.VERIFIED);
        entityManager.merge(pendingReg);

        // 6. Gửi thông báo xác nhận
        sendConfirmationNotification(account, pendingReg.getDisplayName());

        entityManager.flush();

        return account;
    }

    /**
     * Gửi thông báo xác nhận đã đăng ký thành công.
     */
    private void sendConfirmationNotification(Account account, String displayName) {
        String title = "Xác nhận đăng ký thành viên";
        String content = String.format(
                "Xin chào %s,\n\n" +
                "Chúng tôi đã xác nhận đăng ký thành viên của bạn.\n" +
                "Tài khoản đang trong trạng thái chờ phê duyệt.\n" +
                "Chúng tôi sẽ thông báo cho bạn khi đơn được xử lý.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống Hane Web App",
                displayName
        );

        notificationService.sendNotification(
                account,
                title,
                content,
                NotificationChannel.IN_APP
        );
    }

    /**
     * Generate unique member code.
     */
    private String generateMemberCode() {
        String code;
        do {
            code = "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (memberProfileRepository.existsByMemberCode(code));
        return code;
    }
}
