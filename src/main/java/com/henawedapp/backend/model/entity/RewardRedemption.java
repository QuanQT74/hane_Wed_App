package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * RewardRedemption entity - Ánh xạ tới bảng "reward_redemption".
 * Lịch sử đổi quà của hội viên.
 */
@Entity
@Table(name = "reward_redemption")
public class RewardRedemption {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái đổi quà.
     */
    public enum RedemptionStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

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
     * Phần thưởng được đổi.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    /**
     * Số điểm đã trừ.
     */
    @Column(name = "required_points", nullable = false)
    private Integer requiredPoints;

    /**
     * Trạng thái đổi quà.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RedemptionStatus status = RedemptionStatus.PENDING;

    /**
     * Thời gian đổi quà.
     */
    @Column(name = "redeemed_at", nullable = false)
    private Instant redeemedAt;

    /**
     * Người duyệt.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Account approvedBy;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.redeemedAt == null) this.redeemedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected RewardRedemption() {
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

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public Integer getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(Integer requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public RedemptionStatus getStatus() {
        return status;
    }

    public void setStatus(RedemptionStatus status) {
        this.status = status;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public Account getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Account approvedBy) {
        this.approvedBy = approvedBy;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "RewardRedemption{" +
                "id=" + id +
                ", requiredPoints=" + requiredPoints +
                ", status=" + status +
                ", redeemedAt=" + redeemedAt +
                '}';
    }
}
