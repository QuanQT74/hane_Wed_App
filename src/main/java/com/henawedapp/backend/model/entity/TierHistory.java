package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * TierHistory entity - Ánh xạ tới bảng "tier_history".
 * Lịch sử thay đổi hạng hội viên.
 */
@Entity
@Table(name = "tier_history")
public class TierHistory {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hồ sơ hội viên.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Hạng cũ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_tier_id")
    private MembershipTier fromTier;

    /**
     * Hạng mới.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_tier_id", nullable = false)
    private MembershipTier toTier;

    /**
     * Người thực hiện thay đổi.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private Account changedBy;

    /**
     * Lý do thay đổi.
     */
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    /**
     * Thời gian thay đổi.
     */
    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.changedAt == null) this.changedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected TierHistory() {
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

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public MembershipTier getFromTier() {
        return fromTier;
    }

    public void setFromTier(MembershipTier fromTier) {
        this.fromTier = fromTier;
    }

    public MembershipTier getToTier() {
        return toTier;
    }

    public void setToTier(MembershipTier toTier) {
        this.toTier = toTier;
    }

    public Account getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Account changedBy) {
        this.changedBy = changedBy;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "TierHistory{" +
                "id=" + id +
                ", changeReason='" + changeReason + '\'' +
                ", changedAt=" + changedAt +
                '}';
    }
}
