package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * MembershipApplication entity - Ánh xạ tới bảng "membership_application".
 * @Entity đánh dấu đây là JPA Entity
 * @Table chỉ định tên bảng trong database
 */
@Entity
@Table(name = "membership_application")
public class MembershipApplication {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái đơn xin hội viên.
     */
    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    // ============================================================
    // FIELDS
    // ============================================================

    /**
     * Khóa chính: UUID 16-byte dạng binary.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Liên kết tới Account của người nộp đơn.
     * @ManyToOne thiết lập quan hệ nhiều-1 với Account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * Loại hội viên được yêu cầu.
     */
    @Column(name = "requested_member_type", nullable = false, length = 50)
    private String requestedMemberType;

    /**
     * Trạng thái đơn, mặc định PENDING.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    /**
     * Thời gian nộp đơn.
     */
    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    /**
     * Thời gian duyệt đơn.
     */
    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    /**
     * Tài khoản người duyệt đơn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Account reviewedBy;

    /**
     * Lý do từ chối đơn.
     */
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    /**
     * Thời gian tạo bản ghi.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Thời gian cập nhật bản ghi.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.submittedAt == null) this.submittedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected MembershipApplication() {
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getRequestedMemberType() {
        return requestedMemberType;
    }

    public void setRequestedMemberType(String requestedMemberType) {
        this.requestedMemberType = requestedMemberType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Account getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Account reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "MembershipApplication{" +
                "id=" + id +
                ", account=" + (account != null ? account.getId() : null) +
                ", requestedMemberType='" + requestedMemberType + '\'' +
                ", status=" + status +
                ", submittedAt=" + submittedAt +
                ", reviewedAt=" + reviewedAt +
                ", rejectReason='" + rejectReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
