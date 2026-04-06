package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * ReferralLink entity - Ánh xạ tới bảng "referral_link".
 * Link giới thiệu của hội viên.
 */
@Entity
@Table(name = "referral_link")
public class ReferralLink {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hồ sơ hội viên sở hữu link.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Mã giới thiệu duy nhất.
     */
    @Column(name = "referral_code", unique = true, nullable = false, length = 100)
    private String referralCode;

    /**
     * URL chia sẻ.
     */
    @Column(name = "share_url", columnDefinition = "TEXT")
    private String shareUrl;

    /**
     * Trạng thái kích hoạt.
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;

    /**
     * Thời gian tạo.
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
    // CONSTRUCTOR
    // ============================================================

    protected ReferralLink() {
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

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "ReferralLink{" +
                "id=" + id +
                ", referralCode='" + referralCode + '\'' +
                ", shareUrl='" + shareUrl + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
