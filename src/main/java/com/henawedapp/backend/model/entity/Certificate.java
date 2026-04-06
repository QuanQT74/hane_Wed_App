package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Certificate entity - Ánh xạ tới bảng "certificate".
 * Chứng chỉ của hội viên.
 */
@Entity
@Table(name = "certificate")
public class Certificate {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái chứng chỉ.
     */
    public enum CertificateStatus {
        ISSUED,
        REVOKED
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hồ sơ hội viên được cấp chứng chỉ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Loại chứng chỉ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_type_id", nullable = false)
    private CertificateType certificateType;

    /**
     * Số chứng chỉ, duy nhất.
     */
    @Column(name = "certificate_no", unique = true, nullable = false, length = 100)
    private String certificateNo;

    /**
     * Mã xác minh, duy nhất.
     */
    @Column(name = "verification_code", unique = true, nullable = false, length = 100)
    private String verificationCode;

    /**
     * Ngày cấp.
     */
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    /**
     * Trạng thái chứng chỉ.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CertificateStatus status = CertificateStatus.ISSUED;

    /**
     * Người cấp chứng chỉ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private Account issuedBy;

    /**
     * Thời gian tạo bản ghi.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = Instant.now();
    }
    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", certificateNo='" + certificateNo + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", issueDate=" + issueDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
