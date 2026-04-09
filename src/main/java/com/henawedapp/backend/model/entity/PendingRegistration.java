package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.MemberType;
import com.henawedapp.backend.model.enums.PendingRegistrationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * PendingRegistration - Lưu thông tin đăng ký tạm thời trước khi xác thực OTP.
 * Entity này lưu trữ tạm thời thông tin đăng ký của người dùng trong quá trình xác thực OTP.
 * Chỉ sau khi OTP được xác thực thành công, hệ thống mới tạo Account thực sự.
 */
@Entity
@Table(name = "pending_registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"otp"})
public class PendingRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Email hoặc phone của người đăng ký.
     * Unique constraint vì mỗi email/phone chỉ có một đăng ký tạm tại một thời điểm.
     */
    @Column(name = "contact_value", nullable = false, length = 255)
    private String contactValue;

    /**
     * Loại contact: EMAIL hoặc PHONE.
     */
    @Column(name = "contact_type", nullable = false, length = 20)
    private String contactType;

    /**
     * Mật khẩu đã được hash (BCrypt).
     */
    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    /**
     * Tên hiển thị của người đăng ký.
     */
    @Column(name = "display_name", nullable = false)
    private String displayName;

    /**
     * Bio giới thiệu bản thân.
     */
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    /**
     * Loại hội viên: INDIVIDUAL hoặc ORGANIZATION.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false, length = 20)
    private MemberType memberType;

    /**
     * Trạng thái đăng ký tạm.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PendingRegistrationStatus status = PendingRegistrationStatus.PENDING;

    /**
     * OTP liên kết với đăng ký này.
     */
    @OneToOne(mappedBy = "pendingRegistration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Otp otp;

    /**
     * Số lần resend OTP đã thực hiện.
     */
    @Column(name = "resend_count")
    @Builder.Default
    private Integer resendCount = 0;

    /**
     * Thời điểm hết hạn của đăng ký tạm.
     * Mặc định 15 phút kể từ khi tạo.
     */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ========== Helper Methods ==========

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public void incrementResendCount() {
        this.resendCount = (this.resendCount == null ? 0 : this.resendCount) + 1;
    }
}
