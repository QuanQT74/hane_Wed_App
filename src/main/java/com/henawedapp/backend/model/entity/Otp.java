package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.OtpStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Otp - Lưu thông tin OTP cho xác thực đăng ký.
 * Không lưu plain text OTP mà chỉ lưu hash để so sánh.
 */
@Entity
@Table(name = "otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"pendingRegistration"})
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * PendingRegistration liên kết với OTP này.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_registration_id", nullable = false, unique = true)
    private PendingRegistration pendingRegistration;

    /**
     * Hash của OTP (6 số).
     * Dùng BCrypt hoặc SHA-256 để hash trước khi lưu.
     */
    @Column(name = "otp_hash", nullable = false, columnDefinition = "TEXT")
    private String otpHash;

    /**
     * Số lần thử tối đa cho OTP này.
     */
    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 5;

    /**
     * Số lần đã thử.
     */
    @Column(name = "attempts")
    @Builder.Default
    private Integer attempts = 0;

    /**
     * Trạng thái OTP.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private OtpStatus status = OtpStatus.ACTIVE;

    /**
     * Thời điểm hết hạn của OTP.
     * Mặc định 5 phút kể từ khi tạo.
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

    public boolean isMaxAttemptsExceeded() {
        return this.attempts >= this.maxAttempts;
    }

    public void incrementAttempts() {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;
    }

    public void markAsUsed() {
        this.status = OtpStatus.USED;
    }

    public void markAsExpired() {
        this.status = OtpStatus.EXPIRED;
    }

    public void markAsMaxAttemptsExceeded() {
        this.status = OtpStatus.MAX_ATTEMPTS_EXCEEDED;
    }
}
