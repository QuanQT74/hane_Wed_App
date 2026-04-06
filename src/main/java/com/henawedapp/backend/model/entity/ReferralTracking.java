package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * ReferralTracking entity - Ánh xạ tới bảng "referral_tracking".
 * Theo dõi lịch sử giới thiệu.
 */
@Entity
@Table(name = "referral_tracking")
public class ReferralTracking {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái giới thiệu.
     */
    public enum ReferralStatus {
        CLICKED,
        REGISTERED,
        QUALIFIED,
        REWARDED
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Link giới thiệu.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referral_link_id", nullable = false)
    private ReferralLink referralLink;

    /**
     * Hội viên giới thiệu.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_member_id", nullable = false)
    private MemberProfile referrerMember;

    /**
     * Tài khoản được giới thiệu.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_account_id")
    private Account referredAccount;

    /**
     * Trạng thái giới thiệu.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReferralStatus status = ReferralStatus.CLICKED;

    /**
     * Thời gian click.
     */
    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    /**
     * Thời gian đăng ký.
     */
    @Column(name = "registered_at")
    private Instant registeredAt;

    /**
     * Thời gian đủ điều kiện.
     */
    @Column(name = "qualified_at")
    private Instant qualifiedAt;

    /**
     * Thời gian nhận thưởng.
     */
    @Column(name = "rewarded_at")
    private Instant rewardedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.clickedAt == null) this.clickedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected ReferralTracking() {
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

    public ReferralLink getReferralLink() {
        return referralLink;
    }

    public void setReferralLink(ReferralLink referralLink) {
        this.referralLink = referralLink;
    }

    public MemberProfile getReferrerMember() {
        return referrerMember;
    }

    public void setReferrerMember(MemberProfile referrerMember) {
        this.referrerMember = referrerMember;
    }

    public Account getReferredAccount() {
        return referredAccount;
    }

    public void setReferredAccount(Account referredAccount) {
        this.referredAccount = referredAccount;
    }

    public ReferralStatus getStatus() {
        return status;
    }

    public void setStatus(ReferralStatus status) {
        this.status = status;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Instant getQualifiedAt() {
        return qualifiedAt;
    }

    public void setQualifiedAt(Instant qualifiedAt) {
        this.qualifiedAt = qualifiedAt;
    }

    public Instant getRewardedAt() {
        return rewardedAt;
    }

    public void setRewardedAt(Instant rewardedAt) {
        this.rewardedAt = rewardedAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "ReferralTracking{" +
                "id=" + id +
                ", status=" + status +
                ", clickedAt=" + clickedAt +
                ", registeredAt=" + registeredAt +
                ", qualifiedAt=" + qualifiedAt +
                ", rewardedAt=" + rewardedAt +
                '}';
    }
}
